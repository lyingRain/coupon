package com.imooc.coupon.feign.hystrix;

import com.imooc.coupon.feign.SettlementClient;
import com.imooc.coupon.vo.CommonResponse;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 结算微服务熔断策略实现
 * @author zzxstart
 * @date 2020/8/3 - 0:45
 */
@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {
    @Override
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlement) throws ClassCastException {
        log.error("[eureka-client-coupon-settlement] computeRule"+"request erroe");
        settlement.setEmploy(false);
        settlement.setCost(-1.0);

        return new CommonResponse<>(
                -1, "[eureka-client-coupon-settlement] request erroe", settlement
        );
    }
}
