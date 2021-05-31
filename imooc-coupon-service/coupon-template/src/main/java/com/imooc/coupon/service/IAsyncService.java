package com.imooc.coupon.service;

import com.imooc.coupon.entity.CouponTemplate;

/**
 * 异步服务接口定义
 * @author zzxstart
 * @date 2020/5/21 - 22:55
 */
public interface IAsyncService {
    //根据模板异步的创建优惠卷码
    void asyncConstructCouponByTemlate(CouponTemplate template);
}
