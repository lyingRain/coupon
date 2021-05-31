package com.imooc.coupon.vo;

import com.imooc.coupon.constant.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 优惠卷规则对象定义
 * @author zzxstart
 * @date 2020/5/7 - 22:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRule {
    //优惠卷过期规则
    private Expitation expitation;
    //折扣
    private Discount discount;
    //每个人最多领几张
    private Integer limitation;
    //使用范围：地域+商品类型
    private Usage usage;
    //权重（可以和哪些优惠卷叠加使用，同一类的优惠卷一定不能叠加）
    private String weight;

    //校验
    public boolean volidate(){
        return  expitation.validate() && discount.validate()
                && limitation>0 && usage.volidate()
                && StringUtils.isNoneEmpty(weight);
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    //有效期规则
    public static class Expitation{
        //有效规则，duiyingperiodType的code字段
        private  Integer period;
        //有效间隔，只对变动性有效期有效
        private Integer gap;
        //优惠卷模板的失效日期，两类规则都有些
        private Long deadLine;
        boolean validate(){

            return null != PeriodType.of(period)&& gap>0&& deadLine>0;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    //折扣，需要与类型配合决定
    public static class Discount{
        //额度：满减，折扣，立减
        private Integer quota;
        //基准：需要满多少才可用
        private Integer base;
        boolean validate(){
            return quota>0 && base>0;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    //使用范围
    public static class Usage{
        //省份
        private String province;
        //城市
        private String city;
        //商品类型：list[]
        private  String goodsType;
        boolean volidate(){
            return StringUtils.isNotEmpty(province)&&
                    StringUtils.isNotEmpty(city)&&
                    StringUtils.isNotEmpty(goodsType);
        }
    }
}
