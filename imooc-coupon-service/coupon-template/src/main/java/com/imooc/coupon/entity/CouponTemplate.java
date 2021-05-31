package com.imooc.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.DistributeTarget;
import com.imooc.coupon.constant.ProductLine;
import com.imooc.coupon.converter.CouponCategoryConverter;
import com.imooc.coupon.converter.DistributeTargetConverter;
import com.imooc.coupon.converter.ProductLineConverter;
import com.imooc.coupon.converter.RuleConcerter;
import com.imooc.coupon.serialization.CouponTemplateSerialize;
import com.imooc.coupon.vo.TemplateRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 优惠卷模板实体类定义：基础属性+规则属性
 * @author zzxstart
 * @date 2020/5/11 - 23:42
 */
@EnableScheduling
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)//自动填充字段
@Table(name = "coupon_template")
@Data
@JsonSerialize(using = CouponTemplateSerialize.class)//自定义序列化器
public class CouponTemplate {
    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//主键策略
    @Column(name = "id",nullable = false)//数据库列标注名称
    //@Basic  属于数据表中的一个类
    //@Transient   该字段不属于数据库表中的一个字段
     private Integer id;
    //是否为可用状态
    @Column(name = "available",nullable = false)
    private Boolean available;
    //是否过期
    @Column(name = "expird",nullable = false)
    private Boolean expired;
    //优惠卷名称
    @Column(name = "name",nullable = false)
    private String name;
    //优惠卷logo
    private  String logo;
    //优惠卷描述
    @Column(name = "intro",nullable = false)
    private String desc;//数据库关键字  所以要用@Column注解对应列
    //优惠劵分类
    @Column(name="category",nullable = false)
    @Convert(converter = CouponCategoryConverter.class)//枚举转换器
    private CouponCategory category;
    //产品线
    @Column(name = "product_line",nullable = false)
    @Convert(converter = ProductLineConverter.class)
    private ProductLine productLine;
    //总数
    @Column(name = "coupon_count",nullable = false)
    private Integer count;
    //创建时间
    @CreatedDate
    @Column(name="create_time",nullable = false)
    private Date creatTime;
    //创建用户
    @Column(name="user_id",nullable = false)
    private Long userId;
    //优惠劵模板的编码
    @Column(name = "template_key",nullable = false)
    private String key;
    //目标用户
    @Column(name="target",nullable = false)
    @Convert(converter = DistributeTargetConverter.class)
    private DistributeTarget target;
    //优惠劵规则
    @Column(name="rule",nullable = false)
    @Convert(converter = RuleConcerter.class)
    private TemplateRule rule;
    public CouponTemplate(String name,String logo,String desc
    ,String category,Integer productLine,Integer count,
                          Long userId,Integer target,
                          TemplateRule rule) {
        this.available = false;
        this.expired = false;
        this.name = name;
        this.logo = logo;
        this.desc = desc;
        this.category = CouponCategory.of(category);
        this.productLine = ProductLine.of(productLine);
        this.count = count;
        this.userId = userId;
        //优惠劵唯一编码=4（产品线和类型）+8（）日期+id（扩充为4位）
        this.key = productLine.toString() + category + new SimpleDateFormat(
                "yyyyMMdd").format(new Date());
        this.target = DistributeTarget.of(target);
        this.rule = rule;

    }

}
