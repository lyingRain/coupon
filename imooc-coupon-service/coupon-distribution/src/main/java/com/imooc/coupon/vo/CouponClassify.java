package com.imooc.coupon.vo;

import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.constant.PeriodType;
import com.imooc.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户优惠卷分类 根据优惠卷的状态
 * @author zzxstart
 * @date 2020/8/3 - 0:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponClassify {
    //可以使用的
    private List<Coupon> usable;
    //已经使用的
    private List<Coupon> used;
    //已经过期的
    private List<Coupon> expired;
    public static CouponClassify classify(List<Coupon> coupons){
        List<Coupon> usable = new ArrayList<>(coupons.size());
        List<Coupon> used = new ArrayList<>(coupons.size());
        List<Coupon> expired = new ArrayList<>(coupons.size());
        coupons.forEach(c -> {
            boolean isTimeExpire;
            long curTime = new Date().getTime();
            if (c.getTemplateSDK().getRule().getExpitation().getPeriod().equals(
                    PeriodType.REGULAR.getCode()
            )){
                isTimeExpire = c.getTemplateSDK().getRule().getExpitation().getDeadLine() <= curTime;
            }else {
                isTimeExpire = DateUtils.addDays(c.getAssignTime(),c.getTemplateSDK().getRule().getExpitation().getGap()).getTime() <= curTime;
            }
            if (c.getStatus() == CouponStatus.USED){
                used.add(c);
            }else if (c.getStatus() == CouponStatus.EXPIRED || isTimeExpire){
                expired.add(c);
            }else {
                usable.add(c);
            }
        });
            return new CouponClassify(usable,used,expired);
    }
}
