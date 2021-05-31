package com.imooc.coupon.vo;

import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.DistributeTarget;
import com.imooc.coupon.constant.ProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 优惠卷模板创建请求对象
 * @author zzxstart
 * @date 2020/5/14 - 0:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRequest {
    //优惠卷名称
    private String name;
    //优惠卷的logo
    private String logo;
    //优惠卷描述
    private String desc;
    //优惠卷分类
    private String category;
    private Integer productLine;
    private Integer count;
    private Long userId;
    private Integer target;
    private TemplateRule rule;
    //判断对象的合法性
    public boolean validate(){
        boolean stringValid = StringUtils.isNotEmpty(name)
                && StringUtils.isNoneEmpty(logo)
                && StringUtils.isNoneEmpty(desc);
        boolean enumValid = null != CouponCategory.of(category)
                && null!= ProductLine.of(productLine)
                && null != DistributeTarget.of(target);
        boolean numValid = count>0 && userId>0;
        return stringValid && enumValid && numValid && rule.volidate();
    }
}
