package com.imooc.coupon.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 定制HTTP消息转换器Convreters
 *
 * @author zzxstart
 * @date 2020/5/5 - 22:16
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(
            List<HttpMessageConverter<?>> converters) {
        //先把转换器清空
        converters.clear();
        //添加转换器             转换器将Java实例类转换为HTTP json流
        converters.add(new MappingJackson2HttpMessageConverter());

    }
}
