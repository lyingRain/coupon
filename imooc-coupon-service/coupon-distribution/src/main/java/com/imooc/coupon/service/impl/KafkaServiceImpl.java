package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.dao.CouponDao;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.service.IKafkaService;
import com.imooc.coupon.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * <h1>kafka相关服务接口实现</h1>
 * 核心思想：将cache中的coupon的状态同步到 DB中
 * @author zzxstart
 * @date 2020/7/31 - 0:45
 */
@Slf4j
@Service
@Component
public class KafkaServiceImpl implements IKafkaService {
    @Autowired
    private CouponDao couponDao;
    /**
     * 消费优惠卷kafka 消息
     * @param record
     */
    @Override
    @KafkaListener(topics = {Constant.TOPIC}, groupId = "imooc-coupon-1")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()){
            Object message = kafkaMessage.get();
            CouponKafkaMessage couponInfo = JSON.parseObject(
                    message.toString(),
                    CouponKafkaMessage.class
            );
            log.info("Receive CouponKafkaMessage: {}", message.toString());
            CouponStatus status = CouponStatus.of(couponInfo.getStatus());
            switch (status){
                case USABLE:
                    break;
                case USED:
                    processUsedCoupons(couponInfo, status);
                    break;
                case EXPIRED:
                    processExpiredCoupons(couponInfo, status );
                    break;

            }
        }
    }

    /**
     *处理已使用的用户优惠卷
     * @param kafkaMessage
     * @param status
     */
    private  void processUsedCoupons(CouponKafkaMessage kafkaMessage,
                                     CouponStatus status){
        processCouponsByStatus(kafkaMessage,status);
    }

    /**
     *处理已过期的用户优惠卷
     * @param kafkaMessage
     * @param status
     */
    private  void processExpiredCoupons(CouponKafkaMessage kafkaMessage,
                                     CouponStatus status){
        processCouponsByStatus(kafkaMessage,status);
    }

    /**
     * 根据状态处理优惠卷信息
     * @param kafkaMessage
     * @param status
     */
    private void processCouponsByStatus(CouponKafkaMessage kafkaMessage,
                                       CouponStatus status){
        List<Coupon> coupons = couponDao.findAllById(kafkaMessage.getIds());
        if (CollectionUtils.isEmpty(coupons) || coupons.size() != kafkaMessage.getIds().size()){
            log.error("Coupon Not Find Right Coupon Info: {}",
                    JSON.toJSONString(kafkaMessage));
            //TODD 发送邮件
            return;
        }
        coupons.forEach(c -> c.setStatus(status));
        log.info("CouponKafkaMessage Op Coupon Count: {}",
                couponDao.saveAll(coupons).size());
    }
}
