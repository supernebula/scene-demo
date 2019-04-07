/*
 Navicat MySQL Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50720
 Source Host           : localhost:3306
 Source Schema         : scene_seckill

 Target Server Type    : MySQL
 Target Server Version : 50720
 File Encoding         : 65001

 Date: 07/04/2019 20:59:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for seckill
-- ----------------------------
DROP TABLE IF EXISTS `seckill`;
CREATE TABLE `seckill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品库存ID',
  `title` varchar(120) NOT NULL COMMENT '商品名称',
  `image` varchar(1000) DEFAULT NULL COMMENT '商品图片',
  `stock_number` bigint(20) NOT NULL COMMENT '库存数量',
  `price` bigint(11) NOT NULL COMMENT '原价，单位:分',
  `cost_price` bigint(10) NOT NULL COMMENT '秒杀价，单位:分',
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '秒杀开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '秒杀结束时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_start_time` (`start_time`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_end_time` (`end_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

-- ----------------------------
-- Records of seckill
-- ----------------------------
BEGIN;
INSERT INTO `seckill` VALUES (1, 'Apple/苹果 iPhone 6s Plus 国行原装苹果6sp 5.5寸全网通4G手机', 'https://g-search3.alicdn.com/img/bao/uploaded/i4/i3/2249262840/O1CN011WqlHkrSuPEiHxd_!!2249262840.jpg_230x230.jpg', 10, 260000, 110000, '2019-04-06 16:30:00', '2019-04-17 16:30:00', '2019-04-07 18:24:55');
INSERT INTO `seckill` VALUES (2, 'ins新款连帽毛领棉袄宽松棉衣女冬外套学生棉服', 'https://gw.alicdn.com/bao/uploaded/i3/2007932029/TB1vdlyaVzqK1RjSZFzXXXjrpXa_!!0-item_pic.jpg_180x180xz.jpg', 10, 20000, 15000, '2018-04-06 16:30:00', '2019-04-17 16:30:00', '2019-04-07 18:24:55');
INSERT INTO `seckill` VALUES (3, '可爱超萌兔子毛绒玩具垂耳兔公仔布娃娃睡觉抱女孩玩偶大号女生 ', 'https://g-search3.alicdn.com/img/bao/uploaded/i4/i2/3828650009/TB22CvKkeOSBuNjy0FdXXbDnVXa_!!3828650009.jpg_230x230.jpg', 4, 16000, 13000, '2019-04-07 20:56:23', '2019-04-17 16:30:00', '2019-04-07 18:24:55');
COMMIT;

-- ----------------------------
-- Table structure for seckill_order
-- ----------------------------
DROP TABLE IF EXISTS `seckill_order`;
CREATE TABLE `seckill_order` (
  `seckill_id` bigint(20) NOT NULL,
  `user_phone` bigint(20) NOT NULL,
  `pay_amount` bigint(11) NOT NULL COMMENT '支付金额，单位:分',
  `state` tinyint(4) NOT NULL DEFAULT '-1' COMMENT '状态标识：-1:无效  0:成功  1:已付款  2:已发货',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`,`user_phone`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀订单表';

-- ----------------------------
-- Records of seckill_order
-- ----------------------------
BEGIN;
INSERT INTO `seckill_order` VALUES (3, 16611112222, 13000, -1, '2019-04-07 20:56:14');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
