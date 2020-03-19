package com.redis.serial.controller;

import com.redis.serial.service.DefaultSerialNumberService;
import com.redis.serial.service.RedissonSerialNumberService;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tanglong
 * @date 2020-03-18
 */
@RestController
public class SerialNumberController {

    @Autowired
    private DefaultSerialNumberService defaultSerialNumberService;

    @Autowired
    private RedissonSerialNumberService redissonSerialNumberService;

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("/getNum/{code}")
    public String getNum(@PathVariable String code) throws Exception {
        return defaultSerialNumberService.generateSerialNumberByModelCode(code);
    }

    @GetMapping("/redis/{code}")
    public String getRedisNum(@PathVariable String code) throws Exception {
        return redissonSerialNumberService.generateSerialNumberByModelCode(code);
    }

    @GetMapping("/set")
    public String setName() {
        RList<String> list = redissonClient.getList("list");
        if (CollectionUtils.isEmpty(list)) {
            list.add("aqweqweqweqew");
            list.add("b123123123123");
            list.add("c12313213213123");
            list.add("d12313213213213");
            list.add("e786757857857578575");
            list.add("f787254645646");
        }
        return list.remove(list.size() - 1);
    }
}
