package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 产品线枚举
 * @author zzxstart
 * @date 2020/5/7 - 22:21
 */
@Getter
@AllArgsConstructor
public enum  ProductLine {
    DAMAO("大猫",1),
    DABAO("大宝",2);
    private String description;
    private Integer code;
    public static ProductLine of(Integer code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean ->bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code+"not exists"));

    }
}
