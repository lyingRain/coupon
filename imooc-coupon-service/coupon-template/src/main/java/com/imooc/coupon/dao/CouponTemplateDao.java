package com.imooc.coupon.dao;

import com.imooc.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 接口定义
 * @author zzxstart
 * @date 2020/5/13 - 23:24
 */
public interface CouponTemplateDao extends JpaRepository<CouponTemplate,Integer> {
    //根据模板名称查询模板
    CouponTemplate findByName(String name);
    List<CouponTemplate> findAllByAvailableAndExpired(
            Boolean available,Boolean expired
    );

    List<CouponTemplate> findAllByExpired(Boolean expired);
}
