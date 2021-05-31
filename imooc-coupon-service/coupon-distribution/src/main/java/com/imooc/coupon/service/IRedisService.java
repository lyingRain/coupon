package com.imooc.coupon.service;

import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;

import java.util.List;

/**
 * Redis相关服务接口定义
 * 用户的三个状态优惠卷 Cache相关操作
 * 优惠卷模板生成的优惠卷码Cacha操作
 * @author zzxstart
 * @date 2020/7/27 - 23:34
 */
public interface IRedisService {
    /**
     * 根据userId和状态找到缓存的优惠卷列表数据
     * @param userId
     * @param status 优惠卷状态
     * @return {@link Coupon} 注意可能返回null 该用户代表从没有过查找记录
     */
    List<Coupon> getCacheCoupons(Long userId,Integer status);

    /**
     * 保存空的优惠卷列表到缓存中
     * @param userId
     * @param status  优惠卷状态列表
     */
    void saveEmptyCouponListToCache(Long userId, List<Integer> status);

    /**
     * 尝试从cache中获取一个优惠卷码
     * @param templateId  优惠卷模板主键
     * @return  优惠卷码 有可能为空  卷分发完
     */
    String tryToAcquireCouponCodeFromCache(Integer templateId);

    /**
     * 将优惠卷保存到cache中
     * @param userId
     * @param coupons
     * @param status
     * @return    保存成功的个数
     * @throws CouponException
     */
    Integer addCouponToCache(Long userId, List<Coupon> coupons,
                             Integer status) throws CouponException;

}
