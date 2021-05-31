package com.imooc.coupon.constant;

/**
 * 通用常量定义
 *
 * @author zzxstart
 * @date 2020/6/30 - 23:31
 */
public class Constant {
    //kafka消息的Topic
    public static final String TOPIC = "imooc_coupon_op";

    //reids key的前缀定义
    public static class RedisPrefix {
        //当前用户优惠卷码key前缀
        public static final String COUPON_TEMPLATE = "imooc_coupon_template_code_";
        //当前用户可用优惠卷码key前缀
        public static final String USER_COUPON_USABLE = "imooc_user_coupon_usable_";
        //        当前用户用过优惠卷码key前缀
        public static final String USER_COUPON_USED = "imooc_user_coupon_used_";
        //当前用户过期优惠卷码key前缀
        public static final String USER_COUPON_EXPIRED = "imooc_user_coupon_expired_";

    }
}
