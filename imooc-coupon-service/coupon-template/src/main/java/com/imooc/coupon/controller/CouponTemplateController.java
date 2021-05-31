package com.imooc.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IBuildTemplateService;
import com.imooc.coupon.service.ITemplateBaseService;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠卷模板相关的功能控制器
 * @author zzxstart
 * @date 2020/7/16 - 0:06
 */
@Slf4j
@RestController
public class CouponTemplateController {
    @Autowired
    private IBuildTemplateService buildTemplateService;
    @Autowired
    private ITemplateBaseService templateBaseService;

    /**
     * 构建优惠卷模板
     * 127.0.0.1:7001/coupon-template/template/build
     *127.0.0.1:9000/imooc/coupon-template/template/build  通过网关访问接口
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(TemplateRequest request)
           throws Exception{
        log.info("Build Template:{}", JSON.toJSONString(request));
        return buildTemplateService.bulidTemplate(request);
    }

    /**
     * 构造优惠卷模板详情
     * 127.0.0.1:7001/coupon-template/template/info?id=1
     * @param id
     * @return
     * @throws Exception
     */
    @PostMapping("/template/info")
    public CouponTemplate buildTemplateInfo(@RequestParam("id") Integer id) throws
            CouponException{
        log.info("Build Template Info For[]",id);
        return templateBaseService.buildTemplateInfo(id);
    }

    /**
     * 查找所有可用的优惠卷模板
     * @return
     */
    public List<CouponTemplateSDK> findAllUsableTemplate(){
        log.info("find All Usable Template.");
        return templateBaseService.findAllUsableTemplate();
    }

    /**
     * 获取模板IDS 到CouponTemplateSDK 的映射
     * @param ids
     * @return
     */
    @GetMapping("template/sdk/infos")
    public Map<Integer,CouponTemplateSDK> findIdsZTempalte(
       @RequestParam("ids")  Collection<Integer> ids
    ) {
        log.info("FindIdsZTempalteSDK:{}",JSON.toJSONString(ids));
        return templateBaseService.findIds2TemplateSDK(ids);
    }
}
