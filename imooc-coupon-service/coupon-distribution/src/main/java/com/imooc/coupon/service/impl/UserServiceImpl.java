package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.dao.CouponDao;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.feign.SettlementClient;
import com.imooc.coupon.feign.TemplateClient;
import com.imooc.coupon.service.IRedisService;
import com.imooc.coupon.service.IUserService;
import com.imooc.coupon.vo.AcquireTemplateRequest;
import com.imooc.coupon.vo.CouponClassify;
import com.imooc.coupon.vo.CouponKafkaMessage;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务相关的接口实现
 * 所有的操作过程，状态都保存在Redis中，并通过kafka把消息传递到MySql中
 * 为什么使用kafka，如不是直接使用springBoot的异步处理？为了安全性，保证一致性
 * @author zzxstart
 * @date 2020/8/3 - 23:37
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private CouponDao couponDao;
    @Autowired
    private IRedisService redisService;
    //模板服务客户端  idea不能识别造成的
    @Autowired
    private TemplateClient templateClient;
    //结算服务客户端
    @Autowired
    private SettlementClient settlementClient;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException {
        List<Coupon> curCached = redisService.getCacheCoupons(userId, status);
        List<Coupon> preTarget;
        if (CollectionUtils.isNotEmpty(curCached)){
            log.debug("Coupon Cache Is Not Empty: {} {}", userId, status);
            preTarget = curCached;
        }else {
            log.debug("Coupon Cache Is Empty,get coupon from db: {} {}",userId, status);
            List<Coupon> dbCoupons = couponDao.findAllByUserIdAndStatus(
                    userId, CouponStatus.of(status)
            );
            //如果数据库中没有记录，直接返回就可以，Cache中已经加入了一张无效的优惠卷
            if (CollectionUtils.isNotEmpty(dbCoupons)){
                log.debug("current user db not hava coupon : {} {}",userId , status);
                return dbCoupons;
            }
            //填充 dbCoupon的templateSDK字段
            Map<Integer, CouponTemplateSDK> id2TempalteSDK = templateClient.findIds2TemplateSDK(
                    dbCoupons.stream().map(Coupon::getTemplateId).collect(Collectors.toList())
            ).getData();
            dbCoupons.forEach(
                    dc -> dc.setTemplateSDK(id2TempalteSDK.get(dc.getTemplateId()))
            );
            //数据库存在的字段
            preTarget = dbCoupons;
            //将记录写入Cache
            redisService.addCouponToCache(userId, preTarget, status);
        }
        //将无效优惠卷剔除
        preTarget = preTarget.stream().filter(c -> c.getId() != -1).collect(Collectors.toList());
        //如果当前获取的是可用的优惠卷，还需要做对已过期优惠卷的延迟处理
        if (CouponStatus.of(status) == CouponStatus.USABLE){
            CouponClassify classify = CouponClassify.classify(preTarget);
            //如果已过期状态不为空，需要做延期处理
            if (CollectionUtils.isNotEmpty(classify.getExpired())){
                log.info("add expired Coupon To cache from: {} {} ", userId, status);
                redisService.addCouponToCache(userId, classify.getExpired(), CouponStatus.EXPIRED.getCode());
             //发送到kafka中做异步处理
                kafkaTemplate.send(Constant.TOPIC,
                        JSON.toJSONString(new CouponKafkaMessage(
                                CouponStatus.EXPIRED.getCode(),
                                classify.getExpired().stream().map(Coupon::getId).collect(Collectors.toList())
                        )));
            }
            return classify.getUsable();
        }
        return preTarget;
    }

    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {
        long cueTime = new Date().getTime();
        List<CouponTemplateSDK> templateSDKS = templateClient.findAllUsableTemplate().getData();
        log.debug("find all template(from templateClient) count: {}",
                templateSDKS.size());
        //过滤过期的优惠卷模板
        templateSDKS = templateSDKS.stream().filter(
                t -> t.getRule().getExpitation().getDeadLine()> cueTime
        ).collect(Collectors.toList());
        log.info("find usable tempalte count: {}",templateSDKS.size());
        //key是 TemplateId
        //value中的 lift 是 Template limitation(限制最高领取优惠卷), right是优惠劵
        Map<Integer, Pair<Integer, CouponTemplateSDK>> limit2Template =
                new HashMap<>(templateSDKS.size());
        templateSDKS.forEach(
                t -> limit2Template.put(t.getId(), Pair.of(t.getRule().getLimitation(),t))
        );
        List<CouponTemplateSDK> result = new ArrayList<>(limit2Template.size());
        List<Coupon> userUsableCoupons = findCouponsByStatus(
                userId, CouponStatus.USABLE.getCode()
        );
        log.debug("current user has usable coupon:{} {}" , userId, userUsableCoupons.size());
        //key是TemplateId
        Map<Integer, List<Coupon>> tempalte2Coupons = userUsableCoupons.stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));
        //根据 Template 的rule判断是否可以领取优惠卷模板
        limit2Template.forEach((k,v) ->{
            int limitation = v.getLeft();
            CouponTemplateSDK templateSDK = v.getRight();
            if (tempalte2Coupons.containsKey(k) && tempalte2Coupons.get(k).size()>=limitation){
                return;

            }
            result.add(templateSDK);
                }

        );
        return result;
    }

    /**
     * 用户领取优惠卷
     * 1.从TemplateClient 拿到对应的优惠卷，并检查是否过期
     * 2.根据limitation 判断用户是否可以领取
     * 3.save to db
     * 4.填充 CouponTemplateSDK
     * 5.save to cache
     * @param request
     * @return
     * @throws CouponException
     */
    @Override
    public Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException {
        Map<Integer, CouponTemplateSDK> id2Template = templateClient.findIds2TemplateSDK(
                Collections.singletonList(request.getTemplateSDK().getId())
        ).getData();
        //优惠卷模板需要存在的
        if (id2Template.size()<= 0) {
            log.error("can not Acquire template from templateClient: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("can not Acquire template from templateClient");
        }
            //用户是否可以领取这张优惠卷
            List<Coupon> userUsableCoupons = findCouponsByStatus(
                    request.getUserId(), CouponStatus.USABLE.getCode()
            );
            Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons
                    .stream()
                    .collect(Collectors.groupingBy(Coupon::getTemplateId));
            if (templateId2Coupons.containsKey(request.getTemplateSDK().getId())
            && templateId2Coupons.get(request.getTemplateSDK().getId()).size()>=
            request.getTemplateSDK().getRule().getLimitation()){
                log.info("exceed template assign limitation: {}",
                        request.getTemplateSDK().getId());
                throw new CouponException("exceed template assign limitation");
            }
            //尝试获取优惠卷码
            String couponCode = redisService.tryToAcquireCouponCodeFromCache(
                    request.getTemplateSDK().getId()
            );
            if (StringUtils.isEmpty(couponCode)){
                log.error("can not acquire code: {}",request.getTemplateSDK().getId());
                throw new CouponException("can not acquire code");
            }
            Coupon newCoupon = new Coupon(request.getTemplateSDK().getId(),request.getUserId(),couponCode,CouponStatus.USABLE);
            newCoupon = couponDao.save(newCoupon);
            //填充Coupon对象的CouponTempalteSDK,一定要在放入缓存之前填充
        newCoupon.setTemplateSDK(request.getTemplateSDK());
        //放入缓存
        redisService.addCouponToCache(request.getUserId(),
                Collections.singletonList(newCoupon),
                CouponStatus.USABLE.getCode());
        return newCoupon;
    }

    @Override
    public SettlementInfo settlementInfo(SettlementInfo info) throws CouponException {
        return null;
    }
}
