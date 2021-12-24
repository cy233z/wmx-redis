package com.wmx.wmxredis.redisson;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Redssion 操作 redis 5 种基本数据类型
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2021/12/24 20:45
 */
@RestController
@RequestMapping("/redssion/hello")
public class RedssionHelloController {

    @Autowired
    private RedissonClient redissonClient;

    static long i = 20;
    static long sum = 300;

    // ========================== String =======================

    /**
     * http://localhost:8080/redssion/hello/string/set/redssionS
     *
     * @param key
     * @return
     */
    @GetMapping("/string/set/{key}")
    public String s1(@PathVariable String key) {
        // 设置字符串
        RBucket<String> keyObj = redissonClient.getBucket(key);
        keyObj.set(key + "1-v1");
        return key;
    }

    @GetMapping("/get/{key}")
    public String g1(@PathVariable String key) {
// 设置字符串
        RBucket<String> keyObj = redissonClient.getBucket(key);
        String s = keyObj.get();
        return s;
    }

}
