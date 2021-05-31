package com.imooc.coupon.vo;

import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.DistributeTarget;
import com.imooc.coupon.constant.ProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 微服务之间用的优惠卷模板信息定义
 * @author zzxstart
 * @date 2020/5/21 - 23:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponTemplateSDK {
    //优惠卷模板主键
    private Integer id ;
    //优惠卷模板信息
    private String msg;
    //优惠卷logo
    private String logo;
    //优惠卷描述
    private  String desc;
    //优惠卷分类
    private String category;
    //产品线
    private Integer productLine;
    //优惠卷模板的编码
    private String key;
    //优惠卷模板的名称
    private String name;

    //目标用户
    private Integer target;
    //优惠卷规则
    private TemplateRule rule;

}
