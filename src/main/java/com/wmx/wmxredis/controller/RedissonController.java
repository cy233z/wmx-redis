package com.wmx.wmxredis.controller;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Redisson 分布式锁演示控制层
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2020/9/24 19:38
 */
@RestController
public class RedissonController {
    private static Logger logger = LoggerFactory.getLogger(RedissonController.class);

    @Resource
    private RedissonClient redissonClient;

    /**
     * 支付：http://localhost:8080/redisson/payment?orderNumber=885867878
     *
     * @param orderNumber ：订单号
     * @return
     */
    @GetMapping("redisson/payment")
    public String payment(@RequestParam Integer orderNumber) {
        String result = "支付成功!";
        String key = "com.wmx.wmxredis.controller.RedissonController.payment_" + orderNumber;
        RLock lock = redissonClient.getLock(key);
        lock.lock();
        try {
            logger.info(": 查询支付人账户余额.");
            TimeUnit.SECONDS.sleep(2);
            logger.info(": 查询收款人账户状态.");
            TimeUnit.SECONDS.sleep(2);
            logger.info(": 向收款人支付人民币=" + orderNumber);
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
            result = "支付失败：" + e.getMessage();
        } finally {
            lock.unlock();
        }
        return result;
    }

}
