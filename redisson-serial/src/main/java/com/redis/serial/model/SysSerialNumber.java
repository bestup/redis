package com.redis.serial.model;

import lombok.Data;

/**
 * @author tanglong
 * @date 2020-03-11
 */
@Data
public class SysSerialNumber {

    /**
     * id
     */
    private Long id;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 模块编码
     */
    private String moduleCode;

    /**
     * 流水号配置模板
     */
    private String configTemplet;

    /**
     * 序列号最大值
     */
    private Integer maxSerial;

    /**
     * 是否自动增长标示
     */
    private Integer isAutoIncrement;

    /**
     * 预生成流水号数量
     */
    private Integer preMaxNum;

    /**
     * 序列号的长度
     */
    private Integer serialLength;

}
