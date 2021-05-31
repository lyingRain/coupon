package com.imooc.coupon.service;

import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠卷模板基础（view, delete...）服务定义
 * @author zzxstart
 * @date 2020/5/21 - 23:24
 */

public interface ITemplateBaseService {
    /**
     *<h2>根据优惠卷模板 id 获取优惠卷模板信息</h2>
     * @param id
     * @return
     * @throws ClassCastException
     */
    CouponTemplate buildTemplateInfo(Integer id) throws ClassCastException, CouponException;

    /**
     * 查找所有可用的优惠卷模板
     * @return
     */
    List<CouponTemplateSDK> findAllUsableTemplate();


    /**
     * <h2>获取模板Ids到 CouponTemplateSDK 的映射</h2>
     * @param ids  模板ids
     * @return
     */
    Map<Integer,CouponTemplateSDK> findIds2TemplateSDK(
            Collection<Integer> ids
    );

}
