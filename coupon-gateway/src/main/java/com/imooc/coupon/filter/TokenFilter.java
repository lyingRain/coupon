package com.imooc.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 校验请求中传递的token
 * @author zzxstart
 * @date 2020/5/4 - 23:15
 */
@Component
@Slf4j
                               //功能服务响应之前
public class TokenFilter extends AbstractPreZuulfilter{

    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();
        log.info(String.format("%s request to %s",
                request.getMethod(),request.getRequestURI().toString()));
        Object token = request.getParameter("token");
        if (null == token){
            log.error("error: token is empty");
            return fail(401,"error: token is empty");
        }

        return success();
    }

    @Override
    //执行顺序
    public int filterOrder() {
        return 1;
    }
}
