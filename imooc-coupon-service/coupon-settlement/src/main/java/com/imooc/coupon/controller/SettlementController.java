package com.imooc.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.executor.ExecuteManager;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 结算服务接口
 * @author zzxstart
 * @date 2020/8/6 - 21:50
 */
@Slf4j
@RestController
public class SettlementController {
    //结算规则管理器
    @Autowired
    private ExecuteManager executeManager;

    /**
     * 优惠卷结算
     * @param settlement
     * @return
     * @throws ClassCastException
     */
    @PostMapping("/settlement/compute")
    public SettlementInfo comuteRule(SettlementInfo settlement) throws CouponException {
        log.info("Settlement: {}", JSON.toJSONString(settlement));
        return executeManager.computeRule(settlement);
    }
}
