package com.imooc.coupon.service.impl;

import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.ITemplateBaseService;
import com.imooc.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 优惠卷模板基础服务接口实现
 *
 * @author zzxstart
 * @date 2020/7/7 - 23:47
 */
@Service
@Slf4j
public class TemplateBaseServiceImpl implements ITemplateBaseService {
    @Autowired
    private CouponTemplateDao templateDao;

    /**
     * <h2>根据优惠卷模板 id 获取优惠卷模板信息</h2>
     *
     * @param id
     * @return
     * @throws ClassCastException
     */
    @Override
    public CouponTemplate buildTemplateInfo(Integer id) throws CouponException {
        Optional<CouponTemplate> template = templateDao.findById(id);
        if (!template.isPresent()) {
                throw new CouponException("Not Exist Template");
        }
        return template.get();
    }

    @Override
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        List<CouponTemplate> templates =
                templateDao.findAllByAvailableAndExpired(true, false);
        return templates.stream().map(this::template2TemplateSDK)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids) {
        List<CouponTemplate> templates = templateDao.findAllById(ids);
        return templates.stream().map(this::template2TemplateSDK)
                                                                    //CouponTemplateSDK本身
                .collect(Collectors.toMap(CouponTemplateSDK::getId, Function.identity()));
    }


//将CouponTemplate 转换为 CouponTempelateSDK

    private CouponTemplateSDK template2TemplateSDK(CouponTemplate template) {
        return new CouponTemplateSDK(
                template.getId(),
                null,
                template.getLogo(),
                template.getDesc(),
                template.getCategory().getCode(),
                template.getProductLine().getCode(),
                template.getKey(),  //并不是拼装好的key
                template.getName(),
                template.getTarget().getCode(),
                template.getRule()
        );
    }
}
