package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 结算信息定义
 * @author zzxstart
 * @date 2020/7/28 - 0:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettlementInfo {
    //用户Id
    private Long userId;
    //优惠卷列表
    private List<CouponAndTemplateInfo> couponAndTemplateInfos;
    //是否使结算生效，即核销
    private Boolean employ;
    //商品信息
    private List<GoodsInfo> goodsInfos;
    //结果结算金额
    private Double cost;



    /**
     * 优惠卷和模板信息
     */
    @Data
    public static class CouponAndTemplateInfo{
        //coupon主键
        private Integer id;
        //优惠卷对应的模板对象
        private CouponTemplateSDK template;
    }
}
