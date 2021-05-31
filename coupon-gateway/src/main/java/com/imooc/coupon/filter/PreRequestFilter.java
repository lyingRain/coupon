package com.imooc.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**在过滤器中存储客户端发起请求的时间戳
 * @author zzxstart
 * @date 2020/5/5 - 21:05
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class PreRequestFilter extends AbstractPreZuulfilter{
    @Override
    protected Object cRun() {
        context.set("startTime",System.currentTimeMillis());
        return success();
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
