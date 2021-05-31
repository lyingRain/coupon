package com.imooc.coupon.executor.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.coupon.vo.GoodsInfo;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 满减+折扣优惠卷结算满减规则执行器
 * @author zzxstart
 * @date 2020/8/6 - 17:00
 */
@Slf4j
@Service
public class ManJianZheKouExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleFlag() {
        return RuleFlag.MANJIAN_ZHEKOU;
    }

    /**
     * 校验商品类型与优惠卷是否匹配
     * 多品类重载此方法
     * @param settlement {@link SettlementInfo} 用户传递的计算信息
     * @return
     */
    @Override
    protected boolean isCoodsTypeSatisfy(SettlementInfo settlement) {
        log.debug("Check ManJian And ZheKou Is Match Or Not!");
        List<Integer> goodsType = settlement.getGoodsInfos().stream()
                .map(GoodsInfo::getType).collect(Collectors.toList());
        List<Integer> tempalteGoodsType = new ArrayList<>();
        settlement.getCouponAndTemplateInfos().forEach(ct ->{
            tempalteGoodsType.addAll(JSON.parseObject(
                    ct.getTemplate().getRule().getUsage().getGoodsType(),
                    List.class
            ));
                }
        );
        //如果想要使用多类优惠卷，则必须要所有的商品类型都包括在内，即差级为空
        return CollectionUtils.isEmpty(CollectionUtils.subtract(
                goodsType, tempalteGoodsType
        ));
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {
        double goodsSum = retain2Decimals(goodsCostSum(
                settlement.getGoodsInfos()
        ));
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum
        );
        if (null != probability){
            log.debug("ManJian And ZheKou Is Not Match To GoodsType" );
            return probability;
        }
        SettlementInfo.CouponAndTemplateInfo manJian = null;
        SettlementInfo.CouponAndTemplateInfo zheKou = null;
        for (SettlementInfo.CouponAndTemplateInfo ct: settlement.getCouponAndTemplateInfos()) {

            if (CouponCategory.of(ct.getTemplate().getCategory()) == CouponCategory.MANJIAN){
                manJian = ct;
            }else {
                zheKou = ct;
            }
        }
        assert null !=  manJian;
        assert null !=  zheKou;
        //当前的折扣卷和满减卷如果不能共用（一起使用），清空优惠卷，返回商品原价
        if (! isTemplateCouponShared(manJian, zheKou)){
            log.debug("Current Manjian And ZheKou Can Not Shared!");
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }
        List<SettlementInfo.CouponAndTemplateInfo> ctInfos = new ArrayList<>();
        double manJianBase = (double)manJian.getTemplate().getRule().getDiscount().getBase();
        double manJianQuota = (double)zheKou.getTemplate().getRule().getDiscount().getBase();
        //最终的价格
        double targetSum = goodsSum;
        //先计算满减
        if (targetSum >= manJianBase){
            targetSum -= manJianQuota;
            ctInfos.add(manJian);
        }
        //再打印折扣
        double zheKouQuota = (double) zheKou.getTemplate().getRule().getDiscount().getQuota();
        targetSum *= zheKouQuota *1.0/100;
        ctInfos.add(zheKou);
        settlement.setCouponAndTemplateInfos(ctInfos);
        settlement.setCost(retain2Decimals(targetSum>minCost() ? targetSum : minCost()));
        log.debug("Use ManJian And ZheKou Coupon Make Goods Cost From {} To {}",
                goodsSum, settlement.getCost());
        return settlement;
    }

    private boolean isTemplateCouponShared(SettlementInfo.CouponAndTemplateInfo manJian ,
            SettlementInfo.CouponAndTemplateInfo zheKou ){
        String manJianKey = manJian.getTemplate().getKey()+
                String.format("%04d",manJian.getTemplate().getId());
        String zheKouKey = zheKou.getTemplate().getKey()+
                String.format("%04d",zheKou.getTemplate().getId());
        List<String> allShareKeysForManJian = new ArrayList<>();
        allShareKeysForManJian.add(manJianKey);
        allShareKeysForManJian.addAll(JSON.parseObject(
                manJian.getTemplate().getRule().getWeight(), List.class
        ));

        List<String> allShareKeysForZheKou = new ArrayList<>();
        allShareKeysForZheKou.add(zheKouKey);
        allShareKeysForZheKou.addAll(JSON.parseObject(
                zheKou.getTemplate().getRule().getWeight(), List.class
        ));
        //      A 是B的子集  前面为A  后面为B
        return CollectionUtils.isSubCollection(Arrays.asList(manJianKey,zheKouKey),
                allShareKeysForManJian) || CollectionUtils.isSubCollection(
                        Arrays.asList(manJianKey, zheKouKey), allShareKeysForZheKou
        );
    }
}
