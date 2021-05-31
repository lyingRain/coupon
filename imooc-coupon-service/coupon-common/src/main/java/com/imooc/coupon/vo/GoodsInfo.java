package com.imooc.coupon.vo;

import lombok.Data;

/**
 * 商品信息
 * @author zzxstart
 * @date 2020/7/28 - 0:30
 */
@Data
public class GoodsInfo {
    /**
     *  {@link com.imooc.coupon.constant.GoodsType}
     *
     */
    //商品类型
    private Integer type;
    //商品价格
    private Double price;
    //商品数量
    private Integer count;
}
