package com.imooc.coupon.executor.impl;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;
import com.sun.deploy.security.ruleset.Rule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 立减优惠卷结算规则执行器
 * @author zzxstart
 * @date 2020/8/6 - 16:46
 */
@Slf4j
@Service
public class LiJianExecutor extends AbstractExecutor implements RuleExecutor {

    @Override
    public RuleFlag ruleFlag() {
        return RuleFlag.LIJIAN;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {
        double goodsSum = retain2Decimals(goodsCostSum(settlement.getGoodsInfos()));
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum
        );
        if (null != probability){
            log.debug("LiJian Template Is Not Match To GoodsType!");
            return probability;
        }
        //立减优惠卷直接使用， 没有门槛
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos().get(0).getTemplate();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();
        //计算使用优惠卷之后的价格
        settlement.setCost(
                retain2Decimals(goodsSum-quota)>minCost() ? retain2Decimals(goodsSum-quota):minCost()
        );
        log.debug("Use LiJian Coupon Make Goods Cost From {} To {}",
                goodsSum, settlement.getCost());
        return settlement;
    }
}