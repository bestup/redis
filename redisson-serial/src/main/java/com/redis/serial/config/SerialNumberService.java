package com.redis.serial.config;

import com.redis.serial.model.SysSerialNumber;

import java.util.List;

/**
 * @author tanglong
 * @date 2020-03-17
 * @desc 生成序列号的接口定义
 */
public interface SerialNumberService {

    /**
     * 获取序列号
     *
     * @param moduleCode
     * @return
     * @Exception
     */
    String generateSerialNumberByModelCode(String moduleCode) throws Exception;

    /**
     * 根据模块code生成预数量的序列号 默认实现存放到Map中
     *
     * @param sysSerialNumber 模块code
     * @return List<String>
     * @Exception
     */
    List<String> generatePrepareSerialNumbers(SysSerialNumber sysSerialNumber) throws Exception;



}
