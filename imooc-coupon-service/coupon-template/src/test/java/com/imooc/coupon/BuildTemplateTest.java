package com.imooc.coupon;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.DistributeTarget;
import com.imooc.coupon.constant.PeriodType;
import com.imooc.coupon.constant.ProductLine;
import com.imooc.coupon.service.IBuildTemplateService;
import com.imooc.coupon.vo.TemplateRequest;
import com.imooc.coupon.vo.TemplateRule;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * 构造优惠卷模板服务测试
 * @author zzxstart
 * @date 2020/7/17 - 23:57
 */
@SpringBootTest()
@RunWith(SpringRunner.class)
public class BuildTemplateTest {
    @Autowired
    private IBuildTemplateService buildTemplateService;

    @Test
    public void testBuildTemplate() throws Exception{
        System.out.println(JSON.toJSONString(
                buildTemplateService.bulidTemplate(fakeTemplateRequest())
        ));
        Thread.sleep(5000);
    }
    private TemplateRequest fakeTemplateRequest(){
        TemplateRequest request = new TemplateRequest();
        request.setName("优惠卷模板"+new Date().getTime());
        request.setLogo("http://www.imooc.com");
        request.setDesc("只是一张优惠卷模板");
        request.setCategory(CouponCategory.MANJIAN.getCode());
        request.setProductLine(ProductLine.DABAO.getCode());
        request.setCount(10000);
        request.setUserId(10001L);
        request.setTarget(DistributeTarget.SINGLE.getCode());

        TemplateRule rule = new TemplateRule();
        rule.setExpitation(new TemplateRule.Expitation(
                PeriodType.SHIFT.getCode(),
                1, DateUtils.addDays(new Date(),60).getTime()
        ));
        rule.setDiscount(new TemplateRule.Discount(5,1));
        rule.setLimitation(1);
        rule.setUsage(new TemplateRule.Usage(
                "湖北省","武汉市",
                JSON.toJSONString(Arrays.asList("文娱","家具"))
        ));
        rule.setWeight(JSON.toJSONString(Collections.EMPTY_LIST));
        request.setRule(rule);
        return request;
    }


}
