/*
 Navicat MySQL Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : localhost:3306
 Source Schema         : bangbang

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 25/03/2020 16:54:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_serial_number
-- ----------------------------
DROP TABLE IF EXISTS `sys_serial_number`;
CREATE TABLE `sys_serial_number`  (
  `id` bigint(19) NOT NULL,
  `module_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模块名称',
  `module_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模块编码',
  `config_templet` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前模块 使用的序列号模板，以#号分割',
  `serial_length` int(2) NULL DEFAULT 5 COMMENT '序列号的总长度',
  `max_serial` int(10) NULL DEFAULT NULL COMMENT '存放当前序列号的值',
  `pre_max_num` int(4) NULL DEFAULT NULL COMMENT '预生成序列号存放到缓存的个数',
  `is_auto_increment` tinyint(1) NULL DEFAULT 1 COMMENT '自动增长模式步长，默认为1',
  `strategy` tinyint(2) NULL DEFAULT 0 COMMENT '生成策略 1：自动递增 2：每天清零 3：每周清零 4：每月清零',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '流水号生成器' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
