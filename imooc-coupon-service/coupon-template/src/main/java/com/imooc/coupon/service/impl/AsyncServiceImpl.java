package com.imooc.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.service.IAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zzxstart
 * @date 2020/6/30 - 23:57
 */
@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService{
    @Autowired
    private CouponTemplateDao templateDao;
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 根据模板异步的创建优惠卷码
     * @param template
     */
    @Override
    @Async("getAsyncExecutor")
    @SuppressWarnings("all")
    public void asyncConstructCouponByTemlate(CouponTemplate template) {
        Stopwatch watch = Stopwatch.createStarted();
        Set<String> couponCodes = buildCouponCode(template);
        //
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE,template.getId().toString());
        log.info("push CouponCode To Redis:{}",
                redisTemplate.opsForList().rightPushAll(redisKey,couponCodes));
        template.setAvailable(true);
        templateDao.save(template);
        watch.stop();
        log.info("Coupon mil",watch.elapsed(TimeUnit.MILLISECONDS));
        //TODO 发送短信或者邮件通知日志可用
    }
    /**
     * 构建优惠卷码
     * 优惠卷码18位    产品线(3)+类型(1)+日期(6)+随机数(8)
     * @return Set<String> 与template.count相同个数的优惠卷
     */
    @SuppressWarnings("all")
    private Set<String> buildCouponCode(CouponTemplate template){
        Stopwatch watch = Stopwatch.createStarted();
        //set不允许重复
        Set<String> result = new HashSet<>(template.getCount());
        //前四位
        String prefix4 = template.getProductLine().getCode().toString()+
                template.getCategory().getCode();
        String date = new SimpleDateFormat("yyMMdd").format(template.getCreatTime());
        for (int i = 0; i != template.getCount(); ++i) {
            result.add(prefix4+buildCouponCodeSuffix14(date));
        }
        //会出现重复
        while (result.size()<template.getCount()){
            result.add(prefix4+buildCouponCodeSuffix14(date));
        }
        //assert断言    就相当于if
        assert  result.size() == template.getCount();
        watch.stop();
        log.info("build Coupon Code:{}ms",watch.elapsed(TimeUnit.MILLISECONDS));

        return result;
    }
    private String buildCouponCodeSuffix14(String date){
        char[] base = new char[]{'1','2','3','4','5','6','7','8','9'};
        //中间6位
        List<Character> chars = date.chars().mapToObj(e->(char) e).collect(Collectors.toList());
        Collections.shuffle(chars);
        String mid6 = chars.stream().map(Object::toString).collect(Collectors.joining());
        //后八位
        String suffix8 = RandomStringUtils.random(1,base)
                +RandomStringUtils.randomNumeric(7);
        return mid6+suffix8;
    }

}
