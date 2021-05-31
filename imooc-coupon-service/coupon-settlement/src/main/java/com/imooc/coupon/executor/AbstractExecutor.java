package com.imooc.coupon.executor;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.vo.GoodsInfo;
import com.imooc.coupon.vo.SettlementInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 规则执行器抽象类，定义通用方法
 * @author zzxstart
 * @date 2020/8/5 - 23:37
 */
public abstract class AbstractExecutor {
    /**
     * 校验商品类型与优惠卷是否匹配
     * 需要注意：
     * 1.这里实现的是单品类优惠卷的校验，多品类优惠卷重载此方法
     * 2.商品只需要有一个优惠卷要求的商品类型去匹配就可以
     * @param settlement
     * @return
     */
    protected boolean isCoodsTypeSatisfy(SettlementInfo settlement){
        List<Integer> goodsType = settlement.getGoodsInfos()
                .stream()
                .map(GoodsInfo::getType)
                .collect(Collectors.toList());
        List<Integer> templateGoodsType = JSON.parseObject(
                settlement.getCouponAndTemplateInfos().get(0).getTemplate().getRule().getUsage().getGoodsType(),
                List.class
        );
        //存在交集集合
        return CollectionUtils.isNotEmpty(
                //              交集
                CollectionUtils.intersection(goodsType, templateGoodsType)
        );
    }

    /**
     * 处理商品类型与优惠卷限制不匹配的情况
     * @param settlementInfo
     * @param goodsSum
     * @return
     */
    protected SettlementInfo processGoodsTypeNotSatisfy(SettlementInfo settlement, double goodsSum){
        boolean isGoodsTypeSatisfy = isCoodsTypeSatisfy(settlement);
        // 商品类型不满足时，直接返回总价，并清空优惠卷
        if (!isGoodsTypeSatisfy){
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }
        return null;
    }

    /**
     * 商品总价
     * @param goodsInfos
     * @return
     */
    protected double goodsCostSum(List<GoodsInfo> goodsInfos){
        return goodsInfos.stream().mapToDouble(g ->g.getPrice()*g.getCount()).sum();
    }

    /**
     * 保留两位小数
     * @param value
     * @return
     */
    protected double retain2Decimals(double value){
        return new BigDecimal(value).setScale(
                2, BigDecimal.ROUND_HALF_UP
        ).doubleValue();
    }

    //最小支付费用
    protected double minCost(){
        return 0.1;
    }
}
