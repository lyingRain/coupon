package com.imooc.coupon;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.ITemplateBaseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * 优惠卷模板基础服务的测试
 * @author zzxstart
 * @date 2020/7/23 - 23:25
 */
@SpringBootTest()
@RunWith(SpringRunner.class)
public class TemplateBaseTest {
    @Autowired
    private ITemplateBaseService baseService;
    @Test
    public void testBuildTemplateInfo() throws CouponException{
        System.out.println(JSON.toJSONString(
                baseService.buildTemplateInfo(1)
        ));
        System.out.println(JSON.toJSONString(
                baseService.buildTemplateInfo(3)
        ));
    }
    @Test
    public void testFindAllUsableTemplate(){
        System.out.println(JSON.toJSONString(
                baseService.findAllUsableTemplate()
        ));
    }

    @Test
    public void testFindIds2TemplateSDK(){
        System.out.println(JSON.toJSONString(
                baseService.findIds2TemplateSDK(Arrays.asList(1,2,3))
        ));

    }
}
