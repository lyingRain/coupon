package com.imooc.coupon.Scheduled;

import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zzxstart
 * @date 2020/7/15 - 0:05
 */
@Slf4j
@Component
public class ScheduledTask {
    @Autowired
    private CouponTemplateDao templateDao;
    /**
     * 下线已过期的优惠卷模板  固定一小时
     */
    @Scheduled(fixedRate = 60*60*1000)
    public void offlineCouponTemplate(){
        log.info("start To Expire CouponTemplate");
        List<CouponTemplate> templates = templateDao.findAllByExpired(false);
        if (CollectionUtils.isEmpty(templates)){
            log.info("Done To Expire CouponTemplate");
            return;
        }
        Date  cur = new Date();
        List<CouponTemplate> expiredTemplate = new ArrayList<>(templates.size());
        templates.forEach(t->{
            //根据优惠卷规则中的过期规则去校验模板是否过期
            TemplateRule rule = t.getRule();
            if (rule.getExpitation().getDeadLine()<cur.getTime()){
                t.setExpired(true);
                expiredTemplate.add(t);
            }
        });
        if (CollectionUtils.isNotEmpty(expiredTemplate)){
            log.info("Expired CouponTemplate Num:{}",
            templateDao.saveAll(expiredTemplate));
        }
        log.info("Done To Expire CouponTemplate");


    }

}
