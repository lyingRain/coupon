package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 规则类型枚举定义
 * @author zzxstart
 * @date 2020/8/5 - 0:54
 */
@Getter
@AllArgsConstructor
public enum RuleFlag {
    //单类别优惠卷定义
    ManJIAN("满减卷的计算规则"),
    ZHEKOU("折扣卷的计算规则"),
    LIJIAN("立减劵的计算规则"),
   //多类别优惠卷定义
   MANJIAN_ZHEKOU("满减卷+折扣卷的计算规则");
   //TODD 编写更多优惠卷组合
    private String description;
}
