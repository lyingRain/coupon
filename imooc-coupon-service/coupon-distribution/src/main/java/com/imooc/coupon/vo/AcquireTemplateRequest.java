package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获得优惠卷请求对象定义
 * @author zzxstart
 * @date 2020/7/28 - 0:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcquireTemplateRequest {
    private Long userId;
    //优惠卷模板信息
    private CouponTemplateSDK templateSDK;
}
