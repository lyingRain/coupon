package com.imooc.coupon.dao;

import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Coupon   Dao接口定义
 * @author zzxstart
 * @date 2020/7/27 - 23:26
 */
//                                              数据类型     主键类型
public interface CouponDao extends JpaRepository<Coupon,Integer> {
    /**
     * 根据userId 和 status状态查找优惠卷记录
     * @param userId
     * @param status
     * @return
     */
    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);
}
