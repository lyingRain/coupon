package com.imooc.coupon.service;

import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.TemplateRequest;

import javax.smartcardio.CardTerminal;

/**
 * 构建优惠卷模板接口定义
 * @author zzxstart
 * @date 2020/5/14 - 0:37
 */
public interface  IBuildTemplateService {
    //param  模板信息请求对象
    //return  优惠卷模板实体
    CouponTemplate bulidTemplate(TemplateRequest request) throws CouponException;
}
