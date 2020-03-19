package com.redis.serial.service.impl;

import com.redis.serial.constants.SerialConstants;
import com.redis.serial.mapper.SysSerialNumberMapper;
import com.redis.serial.model.SysSerialNumber;
import com.redis.serial.service.DefaultSerialNumberService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author tanglong
 * @date 2020-03-17
 * @desc: 默认实现方式，测试已通过
 */
@Service
public class DefaultSerialNumberServiceImpl implements DefaultSerialNumberService {

    /**
     * 获取序列号加锁
     */
    private ReentrantLock getLock1 = new ReentrantLock();
    private ReentrantLock getLock2 = new ReentrantLock();
    private ReentrantLock getLock3 = new ReentrantLock();

    /**
     * 预生成流水号
     */
    private Map<String, List<String>> prepareSerialNumberMap = new HashMap<>();

    @Autowired
    private SysSerialNumberMapper sysSerialNumberMapper;

    @Override
    public String generateSerialNumberByModelCode(String moduleCode) throws Exception {
        if("V".equals(moduleCode)) {
            getLock1.tryLock(5000, TimeUnit.SECONDS);
            try{
                return generateByModelCode(moduleCode);
            }finally {
                getLock1.unlock();
            }
        }
        if("D".equals(moduleCode)) {
            getLock2.tryLock(5000, TimeUnit.SECONDS);
            try {
                return generateByModelCode(moduleCode);
            }finally {
                getLock2.unlock();
            }
        }
        if("T".equals(moduleCode)) {
            getLock3.tryLock(5000, TimeUnit.SECONDS);
            try {
                return generateByModelCode(moduleCode);
            } finally {
                getLock3.unlock();
            }
        }
        return null;
    }

    public String generateByModelCode(String moduleCode) throws Exception {
        //判断内存中是否还有序列号
        if(!Objects.isNull(prepareSerialNumberMap.get(moduleCode)) && prepareSerialNumberMap.get(moduleCode).size() > 0) {
            //若存在，返回第一个序号
            return prepareSerialNumberMap.get(moduleCode).remove(0);
        }
        SysSerialNumber selSysNumber = new SysSerialNumber();
        selSysNumber.setModuleCode(moduleCode);
        SysSerialNumber sysSerialNumber = sysSerialNumberMapper.get(selSysNumber);
        //生成预序列号，存到缓存中
        List<String> resultList = generatePrepareSerialNumbers(sysSerialNumber);
        prepareSerialNumberMap.put(moduleCode, resultList);
        return prepareSerialNumberMap.get(moduleCode).remove(0);
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
