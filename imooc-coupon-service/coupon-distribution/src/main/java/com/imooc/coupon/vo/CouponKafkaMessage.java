package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 优惠卷kafka 消息对象定义
 * @author zzxstart
 * @date 2020/8/1 - 0:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponKafkaMessage {
    //优惠卷状态
    private Integer status;
    //Coupon主键
    private List<Integer> ids;
}
