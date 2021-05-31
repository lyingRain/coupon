package com.imooc.coupon.advice;

import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 * @author zzxstart
 * @date 2020/5/5 - 23:30
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {
    //d对CommonException进行统一异常处理
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(
            HttpServletRequest req, CouponException ex
    ){
        CommonResponse<String> response = new CommonResponse<>(
                -1,"business error"
        );
        response.setData(ex.getMessage());
        return response;
    }
}
