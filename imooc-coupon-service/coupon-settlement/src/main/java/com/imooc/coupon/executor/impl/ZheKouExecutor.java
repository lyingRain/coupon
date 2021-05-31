package com.imooc.coupon.executor.impl;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 折扣优惠卷结算规则执行器
 * @author zzxstart
 * @date 2020/8/6 - 16:27
 */
@Slf4j
@Service
public class ZheKouExecutor extends AbstractExecutor implements RuleExecutor {
    //规则日志标记
    @Override
    public RuleFlag ruleFlag() {
        return RuleFlag.ZHEKOU;
    }

    /**
     * 优惠卷计算规则
     * @param settlement {@link SettlementInfo} 包含了选择的优惠卷
     * @return
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {
        double goodsSum = retain2Decimals(goodsCostSum(settlement.getGoodsInfos()));
        SettlementInfo probability = processGoodsTypeNotSatisfy(settlement, goodsSum);
        if (null != probability){
            log.debug("ZheKou Template Is Not Match GoodsType!");
            return probability;
        }
        //折扣优惠卷可以直接使用，没有门槛
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos().get(0).getTemplate();
        double quota = (double)templateSDK.getRule().getDiscount().getQuota();
        //计算使用优惠卷后的价格
        settlement.setCost(retain2Decimals((goodsSum*(quota * 1.0 /100))) > minCost() ?
                retain2Decimals((goodsSum*(quota*1.0/100))) : minCost());
        log.debug("Use ZheKou Coupon Make Goods Cost From:{} to{}",
                goodsSum, settlement.getCost());

        return settlement;
    }
}
