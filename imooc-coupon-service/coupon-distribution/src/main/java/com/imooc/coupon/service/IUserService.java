package com.imooc.coupon.service;

import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.AcquireTemplateRequest;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;

import java.util.List;

/**
 * 用户服务相关的接口定义:
 * 1.用户三类状态优惠卷信息展示服务
 * 2.查看用户当前可以领取的优惠卷模板 - couponTemplate 微服务配合
 * 3.用户领取优惠卷服务
 * 4.用户消费优惠卷服务      -coupon-settlement 微服务配合实现
 * @author zzxstart
 * @date 2020/7/27 - 23:57
 */
public interface IUserService {
    /**
     * 根据用户ID和状态查询到优惠卷记录
     * @param userId
     * @param status
     * @return
     * @throws CouponException
     */
    List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException;

    /**
     * 根据用户Id查找当前可以领取的优惠卷模板
     * @param userId
     * @return  {@link CouponTemplateSDK}s
     * @throws CouponException
     */
    List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException;

    /**
     * 用户领取优惠卷服务
     * @param {@link AcquireTemplateRequest}
     * @return
     * @throws CouponException
     */
    Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException;

    /**
     * 结算（核算）优惠卷
     * @param info{{@link SettlementInfo}
     * @return {@link SettlementInfo}
     * @throws CouponException
     */
    SettlementInfo settlementInfo(SettlementInfo info)throws CouponException;
}
