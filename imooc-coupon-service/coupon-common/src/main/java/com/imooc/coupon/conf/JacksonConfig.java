package com.imooc.coupon.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * jackson的自定义配置
 *
 * @author zzxstart
 * @date 2020/5/5 - 22:27
 */
@Configuration
public class JacksonConfig {
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat(
                "yy-MM-dd HH:mm:ss"));
        return mapper;
    }
}
