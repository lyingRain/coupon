package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 分发目标
 * @author zzxstart
 * @date 2020/5/7 - 22:28
 */
@Getter
@AllArgsConstructor
public enum  DistributeTarget {
    SINGLE("单用户",1),
    MUTIL("多用户",2);
    //分发目标的编码
    //分发编码
    private String description;
    private Integer code;

    public static DistributeTarget of(Integer code) {
        return Stream.of(values())
                .filter(bean ->bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code+"not exists"));
    }
}
