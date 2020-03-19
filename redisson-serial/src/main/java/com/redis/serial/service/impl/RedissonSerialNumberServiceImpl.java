package com.redis.serial.service.impl;

import com.redis.serial.constants.SerialConstants;
import com.redis.serial.mapper.SysSerialNumberMapper;
import com.redis.serial.model.SysSerialNumber;
import com.redis.serial.service.RedissonSerialNumberService;
import com.sun.media.jfxmedia.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.redisson.RedissonLock;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author tanglong
 * @date 2020-03-18
 */
@Slf4j
@Service
public class RedissonSerialNumberServiceImpl implements RedissonSerialNumberService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SysSerialNumberMapper sysSerialNumberMapper;

    @Override
    public String generateSerialNumberByModelCode(String moduleCode) throws Exception {
        if("V".equals(moduleCode)) {
            RLock rLock = redissonClient.getLock("Lock" + moduleCode);
            try{
                boolean flag = rLock.tryLock(5, TimeUnit.SECONDS);
                if(flag) {
                    log.info("获取锁成功");
                    return generateByModelCode(moduleCode);
                }
            }finally {
                log.info("释放锁成功");
                rLock.unlock();
            }
        }
        if("D".equals(moduleCode)) {
            RLock rLock = redissonClient.getLock("Lock" + moduleCode);
            rLock.tryLock(5, TimeUnit.SECONDS);
            try {
                return generateByModelCode(moduleCode);
            }finally {
                rLock.unlock();
            }
        }
        if("T".equals(moduleCode)) {
            RLock rLock = redissonClient.getLock("Lock" + moduleCode);
            rLock.tryLock(5, TimeUnit.SECONDS);
            try {
                return generateByModelCode(moduleCode);
            } finally {
                rLock.unlock();
            }
        }
        return null;
    }

    @Override
    public List<String> generatePrepareSerialNumbers(SysSerialNumber sysSerialNumber) throws Exception {
        //临时List变量
        List<String> resultList = new ArrayList<>(sysSerialNumber.getPreMaxNum());
        Integer maxSerialInt = sysSerialNumber.getMaxSerial();
        for(int i = 0; i < sysSerialNumber.getPreMaxNum(); i++){
            //判断是否设置了最大值， 默认为0， 不设最大值
            maxSerialInt  = maxSerialInt + sysSerialNumber.getIsAutoIncrement();
            String code = generatorCode(maxSerialInt, sysSerialNumber);
            resultList.add(code);
        }
        //更新数据
        sysSerialNumber.setMaxSerial(maxSerialInt);
        sysSerialNumberMapper.update(sysSerialNumber);
        return resultList;
    }

    public String generateByModelCode(String moduleCode) throws Exception {
        //判断内存中是否还有序列号
        RList<String> list = redissonClient.getList(moduleCode);
        if(list.size() > 0) {
            //若存在，返回第一个序号
            return list.remove(list.size() - 1);
        }
        SysSerialNumber selSysNumber = new SysSerialNumber();
        selSysNumber.setModuleCode(moduleCode);
        SysSerialNumber sysSerialNumber = sysSerialNumberMapper.get(selSysNumber);
        //生成预序列号，存到缓存中
        List<String> resultList = generatePrepareSerialNumbers(sysSerialNumber);
        for (String code : resultList) {
            list.add(code);
        }
        return list.remove(list.size() - 1);
    }

    private String generatorCode(Integer num, SysSerialNumber sysSerialNumber) {
        String code = null;
        String[] configTempls = new String[]{SerialConstants.YEAR_MONTH, SerialConstants.DATETIME,SerialConstants.NUM,SerialConstants.DATE};
        String[] templs = sysSerialNumber.getConfigTemplet().split(SerialConstants.SEQUENCE);
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < templs.length; i++) {
            if(ArrayUtils.contains(configTempls, templs[i])) {
                switch (templs[i]){
                    case SerialConstants.DATE:
                        stringBuffer.append(DateFormatUtils.format(new Date(), SerialConstants.DATE));
                        break;
                    case SerialConstants.DATETIME:
                        stringBuffer.append(DateFormatUtils.format(new Date(), SerialConstants.DATETIME));
                        break;
                    case SerialConstants.YEAR_MONTH:
                        stringBuffer.append(DateFormatUtils.format(new Date(), SerialConstants.YEAR_MONTH));
                        break;
                    case SerialConstants.NUM:
                        break;
                    default:
                        break;
                }
            }else{
                stringBuffer.append(templs[i]);
            }
        }
        String stringCode = stringBuffer.toString();
        return stringCode + StringUtils.leftPad(String.valueOf(num), sysSerialNumber.getSerialLength() - stringCode.length(), "0");
    }
}
