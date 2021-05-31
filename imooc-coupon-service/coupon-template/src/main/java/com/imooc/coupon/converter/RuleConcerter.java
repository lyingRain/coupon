package com.imooc.coupon.converter;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.vo.TemplateRule;
import com.sun.xml.internal.ws.api.databinding.JavaCallInfo;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

/**
 * @author zzxstart
 * @date 2020/5/13 - 0:13
 */
@Convert
public class RuleConcerter implements AttributeConverter<TemplateRule,
        String> {
    @Override
    public String convertToDatabaseColumn(TemplateRule rule) {
        return JSON.toJSONString(rule);
    }

    @Override
    public TemplateRule convertToEntityAttribute(String rule) {
        return JSON.parseObject(rule,TemplateRule.class);
    }
}
