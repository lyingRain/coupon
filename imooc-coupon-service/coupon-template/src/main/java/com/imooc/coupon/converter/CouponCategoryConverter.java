package com.imooc.coupon.converter;

import com.imooc.coupon.constant.CouponCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

/**
 * 优惠卷分类枚举属性转换器
 * AttributeConverter<X,Y>
 *     X表示实体属性的类型  Y表示数据库字段的类型
 * @author zzxstart
 * @date 2020/5/12 - 23:26
 */
@Convert
public class CouponCategoryConverter implements AttributeConverter<
        CouponCategory,String> {
    //将实体属性X转换为Y存储到数据库中，插入和更新时执行的动作
    @Override
    public String convertToDatabaseColumn(CouponCategory couponCategory) {
        return couponCategory.getCode();
    }
    //将数据库中的字段Y转化为实体属性X，查询操作时执行的动作
    @Override
    public CouponCategory convertToEntityAttribute(String code) {
        return CouponCategory.of(code);
    }
}
