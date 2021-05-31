package com.imooc.coupon.executor;

import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 优惠卷结算规则执行管理器
 * 即根据用户的请求（SettlementInfos）找到对应的Executor ，去做结算
 * BeanPostProcessor :bean 后置处理器
 * @author zzxstart
 * @date 2020/8/6 - 21:05
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class ExecuteManager implements BeanPostProcessor {
    //规则执行器映射
    private static Map<RuleFlag, RuleExecutor> executorIndex =
            new HashMap<>(RuleFlag.values().length);

    /**
     * 优惠卷结算规则计算入口
     * 注意：一定要保证传递进来的优惠卷的个数>=1
     * @param settlement
     * @return
     * @throws CouponException
     */
    public SettlementInfo computeRule(SettlementInfo settlement) throws CouponException {
        SettlementInfo result = null;
        //单类优惠卷
        if (settlement.getCouponAndTemplateInfos().size() == 1){
            //获取优惠卷的类别
            CouponCategory category = CouponCategory.of(
                    settlement.getCouponAndTemplateInfos().get(0).getTemplate().getCategory()
            );
            switch (category){
                case MANJIAN:
                    result = executorIndex.get(RuleFlag.ManJIAN).computeRule(settlement);
                    break;
                case ZHEKOU:
                    result = executorIndex.get(RuleFlag.ZHEKOU).computeRule(settlement);
                     break;
                case LIJIAN:
                    result = executorIndex.get(RuleFlag.LIJIAN).computeRule(settlement);
                      break;
            }
        }else {
            //多类优惠卷
            List<CouponCategory> categories = new ArrayList<>(
                    settlement.getCouponAndTemplateInfos().size()
            );
            settlement.getCouponAndTemplateInfos().forEach(
                    ct -> categories.add(CouponCategory.of(
                            ct.getTemplate().getCategory()
                    ))
            );
            if (categories.size() != 2){
                throw new CouponException("Not Suppert For More"+
                        "Tempalte Category");

            }else {
                if(categories.contains(CouponCategory.MANJIAN) &&
                categories.contains(CouponCategory.ZHEKOU)){
                    result = executorIndex.get(RuleFlag.MANJIAN_ZHEKOU).computeRule(settlement);
                }else {
                    throw new CouponException("Not Suppert For Other"+
                            "Tempalte Category");
                }
            }
        }
        return result;
    }
    /**
     * 在Bean初始化之前去执行
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof RuleExecutor)){
            return bean;
        }
        RuleExecutor executor = (RuleExecutor) bean;
        RuleFlag ruleFlag=executor.ruleFlag();
        if (executorIndex.containsKey(ruleFlag)){
            throw new IllegalStateException("There Is already on executor"+
                    "for rule flag" + ruleFlag);
        }
        log.info("load executor {} for rule flag{}",
                executor.getClass(), ruleFlag);
        executorIndex.put(ruleFlag, executor);
        return null;
    }

    /**
     * 在Bean初始化之后去执行
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }
}
