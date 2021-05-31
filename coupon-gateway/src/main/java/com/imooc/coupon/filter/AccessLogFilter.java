package com.imooc.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求日志过滤器
 * @author zzxstart
 * @date 2020/5/5 - 21:17
 */
@Component
@Slf4j
public class AccessLogFilter extends AbstractPostZuulfilter{
    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();
        Long startTime = (Long)context.get("startTime");
        //从PreRequestFilter中获取时间戳
        String uri = request.getRequestURI();
        long duration = System.currentTimeMillis()-startTime;
        //从网关通过的请求都会打印日志记录：uri+duration
        log.info("uri: {}",uri,duration);
        return success();
    }

    @Override
    public int filterOrder() {
        //在post之前  否则不起作用

        return FilterConstants.SEND_RESPONSE_FILTER_ORDER-1;
    }
}
