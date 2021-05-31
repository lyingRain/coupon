package com.imooc.coupon.feign;

import com.imooc.coupon.feign.hystrix.SettlementClientHystrix;
import com.imooc.coupon.vo.CommonResponse;
import com.imooc.coupon.vo.SettlementInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 优惠卷结算微服务 Feign接口定义
 * @author zzxstart
 * @date 2020/8/3 - 0:16
 */
@FeignClient(value = "eureka-client-coupon-settlement",
        fallback = SettlementClientHystrix.class)


public interface SettlementClient {
    /**
     * 优惠卷规则计算
     * @param settlement
     * @return
     * @throws ClassCastException
     */
    @RequestMapping(value = "/coupon-settlement/settlement/compute", method = RequestMethod.POST)
    CommonResponse<SettlementInfo> computeRule(@RequestBody SettlementInfo settlement) throws ClassCastException;
}
