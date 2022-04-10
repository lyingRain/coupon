package com.imooc.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author zzxstart
 * @date 2020/5/4 - 22:14
 */
@EnableZuulProxy
@SpringCloudApplication
public class ZuulGatewayApplication {
    public static void main(String[] args) {
        System.out.println("test01");
        SpringApplication.run(ZuulGatewayApplication.class, args);
    }
}
