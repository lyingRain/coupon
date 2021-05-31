package com.imooc.coupon.controller;

import com.imooc.coupon.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zzxstart
 * @date 2020/7/15 - 23:17
 */
@Slf4j
@RestController
/**
 * 健康检查接口  127.0.0.1:8007/coupon-template/health
 */
public class HealthCheck {
    //服务发现客户端
    @Autowired
    private DiscoveryClient client;
    //服务注册接口，提供了获取服务id的方法
    @Autowired
    private Registration registration;

    @GetMapping("/health")
    public String health(){
        log.debug("view health api");
        return "CouponTemplate is ok";
    }

    /**
     * 异常测试接口
     * @return
     * @throws Exception
     */
    @GetMapping("/exception")
     public String eaception() throws Exception{
         log.debug("view exception api");
         throw new CouponException("CouponTemplate Has Same Problem");

     }
     //获取Eureka Server 上的微服务元信息
     @GetMapping("/info")
     public List<Map<String,Object>> info(){
        //大约需要两分钟才能获取到注册信息
         List<ServiceInstance> instances = client.getInstances(
                 registration.getServiceId()
         );
         List<Map<String,Object>> result = new ArrayList<>(instances.size());
         instances.forEach(i->{
             Map<String,Object> info = new HashMap<>();
             info.put("serviceId",i.getServiceId());
             info.put("instanceId",i.getInstanceId());
             info.put("port",i.getPort());
             result.add(info);
         });
            return result;
     }

}
