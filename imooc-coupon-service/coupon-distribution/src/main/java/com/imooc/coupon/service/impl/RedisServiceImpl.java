package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * redis相关的操作服务接口实现
 * @author zzxstart
 * @date 2020/7/28 - 23:37
 */
@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public List<Coupon> getCacheCoupons(Long userId, Integer status) {
        log.info("Get Coupon From Cache:{} ,{}",userId,status);
        String redisKey = status2RedisKey(status,userId);
        List<String> couponStrs = redisTemplate.opsForHash().values(redisKey)
                .stream()
                .map(o -> Objects.toString(o,null))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(couponStrs)){
            saveEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Collections.emptyList();
        }
        return couponStrs.stream().map(cs->JSON.parseObject(cs,Coupon.class)).collect(Collectors.toList());
    }

    /**
     * 保存空的优惠卷列表到缓存中
     * 避免缓存穿透 ： 用户发起请求，先去redis缓存中查找，没有再去数据库找
     * 数据库也不存在相应的数据，如果有大量相同的请求同时访问就会造成缓存穿透
     * @param userId
     * @param status  优惠卷状态列表
     */
    @Override
    @SuppressWarnings("all")
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("sava Empty List To User :{} ,Status:{}",
                userId, JSON.toJSONString(status));
        //key是coupon_id,value是序列化Coupon
        Map<String,String> invalidCouponMap = new HashMap<>();
        invalidCouponMap.put("-1",JSON.toJSONString(Coupon.invalidCoupon()));
        //使用Redis中SessionCallBack吧数据命令放到redis的pipeline
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                status.forEach(s -> {
                    String redisKey = status2RedisKey(s,userId);
                    redisOperations.opsForHash().putAll(redisKey,invalidCouponMap);
                });
                return null;
            }
        };
        log.info("Pipoline Exe Result: {}",JSON.toJSONString(
                redisTemplate.executePipelined(sessionCallback)
        ));
    }

    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        String redisKey = String.format("%s%s",Constant.RedisPrefix.COUPON_TEMPLATE,templateId.toString());
        //因为优惠卷码不存在顺序关系，左边pop或者右边pop 没有影响
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);
        log.info("Acquire Coupon Code:{},{},{}",
                templateId,redisKey,couponCode);
        return couponCode;
    }

    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
        log.info("Add Coupon To Cache:{},{},{}",
                userId,JSON.toJSONString(coupons), status);
        Integer result = -1;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus){
            case EXPIRED:
                result = addCouponToCacheForExpired(userId, coupons);
                break;
            case USED:
                result = addCouponToCacheForUsed(userId, coupons);
                break;
            case USABLE:
                result = addCouponToCacheForUsable(userId, coupons);
                break;
        }
        return result;
    }

    /**
     * 新增加优惠卷到cache中
     * @param userId
     * @param coupons
     * @return
     */
    private Integer addCouponToCacheForUsable(Long userId,List<Coupon> coupons){
        //如果status是usable,代表是新增加的优惠卷
        //只会影响到一个Cache:USER_COUPON_USABLE
        log.info("Add Coupon To Cache For Usable");
        Map<String, String> needCacheObject = new HashMap<>();
        coupons.forEach(c -> needCacheObject.put(c.getId().toString(),
                JSON.toJSONString(c)));
        String redisKey = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        redisTemplate.opsForHash().putAll(redisKey,needCacheObject);
        log.info("Add {} Coupons To Cache: {} {}",
                needCacheObject.size(), userId, redisKey);
        redisTemplate.expire(redisKey, getRandomExpiredTime(1,2), TimeUnit.SECONDS);
        return needCacheObject.size();
    }

    /**
     * 将已使用的优惠卷放在缓存中
     * @param userId
     * @param coupons
     * @return
     */
    private Integer addCouponToCacheForUsed(Long userId, List<Coupon> coupons) throws CouponException{
        //如果status 是 USED， 代表用户操作是使用党情的优惠卷，影响到两个Cache
        //USABLE , USED
        log.debug("Add Coupon To Cache For Used");
        Map<String, String> needCacheForUsed = new HashMap<>(coupons.size());
        String redisKeyForUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(),userId
        );
        String redisKeyForUsed = status2RedisKey(
                CouponStatus.USED.getCode(),userId
        );
        //获取当前用户可用的优惠卷
        List<Coupon> curUsableCoupons = getCacheCoupons(userId,
                CouponStatus.USABLE.getCode());
        //当前可用的优惠卷个数大于1  因为存在至少一个空的Coupon
        assert curUsableCoupons.size()>coupons.size();
        coupons.forEach(c -> needCacheForUsed.put(c.getId().toString(),
                JSON.toJSONString(c)));
        //检验当前的优惠卷参数是否与 Cache中的匹配
        List<Integer> curUsableIds = curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream().map(Coupon::getId).collect(Collectors.toList());
        // 该方法意思是 paranIds 是 curUsableIds的子集 加!就是不是
        if (!CollectionUtils.isSubCollection(paramIds, curUsableIds)){
            log.error("CurCoupon is not equal to cache : {} {} {}",
                    userId, JSON.toJSONString(curUsableIds), JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupon is not equal to cache");
        }
        List<String> needCleanKey = paramIds.stream()
                .map(i -> i.toString()).collect(Collectors.toList());
        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public Objects execute(RedisOperations redisOperations) throws DataAccessException {
                //1.已经使用的优惠卷Cache 缓存添加
                redisOperations.opsForHash().putAll(
                        redisKeyForUsed, needCacheForUsed
                );
                //2.可用的优惠卷cache需要清理
                redisOperations.opsForHash().delete(
                        redisKeyForUsable , needCleanKey.toArray()
                );
                //3.重置过期时间
                redisOperations.expire(
                        redisKeyForUsable,
                        getRandomExpiredTime(1,2),
                        TimeUnit.SECONDS
                );
                redisOperations.expire(
                        redisKeyForUsed,
                        getRandomExpiredTime(1,2),
                        TimeUnit.SECONDS
                );

                return null;
            }
        };
            log.info("Pipeling Exe Result: {}",
                   JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
            return coupons.size();
    }

    /**
     * 将过期优惠券加入到Cache中
     * @param userId
     * @param coupons
     * @return
     * @throws CouponException
     */
    @SuppressWarnings("all")
    private Integer addCouponToCacheForExpired(Long userId, List<Coupon> coupons) throws CouponException{
        //status 是EXPIRED，代表是已有的优惠卷过期， 影响到两个Cache
        //USABLE  减法, EXPIRED  加法
        log.debug("Add Coupon To Cache For Expired");
        Map<String, String> needCacheForExpired = new HashMap<>(coupons.size());
        String redisKeyForUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId
        );
        String redisKeyForExpired = status2RedisKey(
                CouponStatus.EXPIRED.getCode(), userId
        );
        List<Coupon> curUsableCoupons = getCacheCoupons(userId, CouponStatus.USABLE.getCode());
        List<Coupon> curExpiredCoupons = getCacheCoupons(userId, CouponStatus.EXPIRED.getCode());
        //当前可用的优惠卷个数一定大于一
        assert curUsableCoupons.size()>coupons.size();
        coupons.forEach(c->needCacheForExpired.put(
                c.getId().toString(), JSON.toJSONString(c)
        ));
        //校验当前的优惠卷参数是否与Cache中匹配
        List<Integer> curUsableIds = curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());

        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        if (!CollectionUtils.isSubCollection(paramIds, curUsableIds)) {
            log.error("CurCoupons Is Not Equal To Cache; {} {} {}",
                    userId,JSON.toJSONString(curUsableIds),JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupon Is Not Equal To Cache");
        }
        List<String> needCleanKey = paramIds.stream()
                .map(i-> i.toString()).collect(Collectors.toList());
        SessionCallback sessionCallback = new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //已经过期的优惠卷 cache 缓存
                redisOperations.opsForHash().putAll(
                        redisKeyForExpired, needCacheForExpired
                );
                //2. 可用的优惠卷需要清理
                redisOperations.opsForHash().delete(
                        redisKeyForUsable, needCleanKey.toArray()
                );
                //重置过期时间
                redisOperations.expire(
                        redisKeyForUsable,
                        getRandomExpiredTime(1,2),
                        TimeUnit.SECONDS
                );
                redisOperations.expire(
                        redisKeyForExpired,
                        getRandomExpiredTime(1,2),
                        TimeUnit.SECONDS
                );

                return null;
            }
        };
        log.info("Pipeline Exe Result: {}",
                JSON.toJSONString(
                        redisTemplate.executePipelined(sessionCallback)
                ));
        return coupons.size();
    }
    /**
     * 根据status获取redis Key
     * @param status
     * @param userId
     * @return
     */
    private String status2RedisKey(Integer status,Long userId){
        String redisKey = null ;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus){
            case USABLE:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_USABLE);
                break;
            case USED:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_USED);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_EXPIRED);
                break;
        }
            return  redisKey;
    }

    /**
     * 获取一个随机的过期时间
     * 缓存雪崩：在同一时间key失效
     * @param min  最小小时数
     * @param max  最大小时数
     * @return   返回【min, max】之间的随机数
     */
    private Long getRandomExpiredTime(Integer min, Integer max){
        return RandomUtils.nextLong(min*60*60,max*60*60);
    }
}
