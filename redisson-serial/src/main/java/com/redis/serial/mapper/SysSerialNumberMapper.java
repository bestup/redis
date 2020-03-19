package com.redis.serial.mapper;

import com.redis.serial.model.SysSerialNumber;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author tanglong
 * @date 2020-03-11
 */
@Mapper
public interface SysSerialNumberMapper {

    /**
     * 新增
     * @param sysSerialNumber
     * @return
     */
    Integer add(SysSerialNumber sysSerialNumber);

    /**
     * 修改
     * @param sysSerialNumber
     * @return
     */
    Integer update(SysSerialNumber sysSerialNumber);

    /**
     * 根据id查询
     * @param sysSerialNumber
     * @return
     */
    SysSerialNumber get(SysSerialNumber sysSerialNumber);
}
