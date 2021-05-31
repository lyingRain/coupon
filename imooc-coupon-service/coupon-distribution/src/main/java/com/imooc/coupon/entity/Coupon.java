package com.imooc.coupon.entity;

import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.converter.CouponStatusConverter;
import com.imooc.coupon.vo.CouponTemplateSDK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.lang.annotation.Target;
import java.util.Date;

/**
 * 优惠卷实体表
 * @author zzxstart
 * @date 2020/7/26 - 0:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Integer id;
    @Column(name = "template_id",nullable = false)
//关联优惠卷模板的主键
    private Integer templateId;
    //领取用户
    @Column(name = "user_id",nullable = false)
    private Long userId;
    //优惠卷码
    @Column(name = "coupon_code",nullable = false)
    private String couponCode;
    //领取时间  用到jpa的审计功能 领取时间就是创建时间
    @CreatedDate
    @Column(name = "assign_time",nullable = false)
    private Date assignTime;
    //优惠卷状态
    @Basic
    @Column(name = "status",nullable = false)
    @Convert(converter = CouponStatusConverter.class)
    private CouponStatus status;
    //用户优惠卷对应的的模板信息
    @Transient
    private CouponTemplateSDK templateSDK;

    public static Coupon invalidCoupon(){
        Coupon coupon = new Coupon();
        coupon.setId(-1);
        return coupon;
    }
    public Coupon(Integer templateId,Long userId,String couponCode,CouponStatus status){
         this.templateId = templateId;
         this.userId = userId;
         this.couponCode = couponCode;
         this.status = status;
    };
}
