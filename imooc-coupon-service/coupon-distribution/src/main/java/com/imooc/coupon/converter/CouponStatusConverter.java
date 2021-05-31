package com.imooc.coupon.converter;

import com.imooc.coupon.constant.CouponStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 状态枚举属性转换器
 * @author zzxstart
 * @date 2020/7/26 - 23:22
 */
@Converter
public class CouponStatusConverter implements
        AttributeConverter<CouponStatus,Integer> {

    @Override
    public Integer convertToDatabaseColumn(CouponStatus status) {
        return status.getCode();
    }

    @Override
    public CouponStatus convertToEntityAttribute(Integer code) {
        return CouponStatus.of(code);
    }
}
