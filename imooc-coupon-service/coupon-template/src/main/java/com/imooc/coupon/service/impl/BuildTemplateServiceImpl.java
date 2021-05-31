package com.imooc.coupon.service.impl;

import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IAsyncService;
import com.imooc.coupon.service.IBuildTemplateService;
import com.imooc.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 构建优惠卷模板实现
 * @author zzxstart
 * @date 2020/7/3 - 0:20
 */
@Slf4j
@Service
public class BuildTemplateServiceImpl implements IBuildTemplateService {
    @Autowired
    private IAsyncService asyncService;
    @Autowired
    private CouponTemplateDao templateDao;

    @Override
    public CouponTemplate bulidTemplate(TemplateRequest request) throws CouponException {
        //参数合法性校验
        if (!request.validate()){
            throw new CouponException("BuildTemplate Param Is Not valid!");
        }
        //判断同名的优惠卷模板是否存在
        if (null != templateDao.findByName(request.getName())){
            throw new CouponException("Exist Same Template");
        }
        //构造CouponTemplate 并保存到数据库中
        CouponTemplate template = requestToTemplate(request);
        template = templateDao.save(template);
        //根据优惠卷模板异步生成优惠卷码
        asyncService.asyncConstructCouponByTemlate(template);
        return template;
    }

    /**
     * j将TemplateRequest转化为CouponTemplate实体
     * @param request
     * @return
     */
    private CouponTemplate requestToTemplate(TemplateRequest request){
        return  new CouponTemplate(request.getName(),
                request.getLogo(),
                request.getDesc(),
                 request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule());
    }

}
