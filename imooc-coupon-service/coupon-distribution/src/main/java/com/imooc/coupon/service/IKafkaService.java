package com.imooc.coupon.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;


/**
 * <h1>kafaka 相关的服务接口定义</h1>
 * @author zzxstart
 * @date 2020/7/27 - 23:50
 */
public interface IKafkaService {
    /**
     * <h2>消费优惠卷kafka消息</h2>
     * ConsumerRecord  监听kafka的topic的一个record记录
     * @param record
     */
    void consumeCouponKafkaMessage(ConsumerRecord<?,?> record);

}
