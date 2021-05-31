package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 优惠卷分类
 * @author zzxstart
 * @date 2020/5/7 - 22:04
 */
@Getter
@AllArgsConstructor
public enum CouponCategory {
    MANJIAN("满减卷","001"),
    ZHEKOU("折扣卷","002"),
    LIJIAN("立减劵","003");
    //描述信息
    private String description;
    private String code;
    public static CouponCategory of(String code){
        Objects.requireNonNull(code);
        //获取枚举数组
        return Stream.of(values())
                //过滤器获得任何一个枚举参数
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code+"not exists") );
    }
}
