--创建coupon_template数据表
CREATE TABLE IF NOT EXISTS `imooc_coupon_data`.`coupou_template`(
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `available` boolean NOT NULL DEFAULT false COMMENT '是否可用状态;true 可用',
  `expired` boolean NOT NULL DEFAULT false COMMENT '是否过期;true 是',
  `name` varcher(64) NOT NULL DEFAULT '' COMMENT '优惠卷名称',
  `logo` varcher(256) NOT NULL DEFAULT '' COMMENT '优惠卷logo',
  `intro` varcher(256) NOT NULL DEFAULT '' COMMENT '优惠卷描述',
  `category` varcher(64) NOT NULL DEFAULT '' COMMENT '优惠卷分类',
  `product_line` varcher(11) NOT NULL DEFAULT '0' COMMENT '产品线',
  `coupon_count` varcher(11) NOT NULL DEFAULT '0' COMMENT '总数',
  `create_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '创建时间',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建用户',
  `template_key` varcher(128) NOT NULL DEFAULT '' COMMENT '优惠卷模板编码',
  `target` int(11) NOT NULL DEFAULT '0' COMMENT '目标用户',
  `rule` varcher(1024) NOT NULL DEFAULT '' COMMENT '优惠卷规则：TemplateRule的json',
  PRIMARY KEY (`id`),
  --单列索引 名称       对应的列
  KEY `idx_category` (`category`),
  KEY  `idx_user_id` (`user_id`),
  --唯一索引
  UNIQUE KEY `name` (`name`)
) ENGINT=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=uts-8 COMMENT='优惠卷模板表';

--清空数据表
--truncate coupon_template;