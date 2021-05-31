package com.imooc.coupon.executor;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.vo.SettlementInfo;

/**
 * 优惠卷模板规则处理器接口定义
 * @author zzxstart
 * @date 2020/8/5 - 1:01
 */
public interface RuleExecutor {
    /**
     * 规则类型的标记
     * @return {@link RuleFlag}
     */
    RuleFlag ruleFlag();

    /**
     * y优惠卷规则的计算
     * @param settlement {@link SettlementInfo} 包含了选择的优惠卷
     * @return {@link SettlementInfo} 修正过的结算信息
     */
    SettlementInfo computeRule(SettlementInfo settlement);
}
