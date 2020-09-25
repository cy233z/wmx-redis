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
     * 未加锁做任何限制时
     * <p>
     * 支付：http://localhost:8080/redisson/payment1?orderNumber=885867878
     *
     * @param orderNumber ：订单号
     * @return
     */
    @GetMapping("redisson/payment1")
    public String payment1(@RequestParam Integer orderNumber) {
        String result = "订单【" + orderNumber + "】支付成功!";
        logger.info("用户请求支付订单【" + orderNumber + "】");
        try {
            logger.info(": 查询支付状态.");
            TimeUnit.SECONDS.sleep(4);
            logger.info(": 开始支付订单【" + orderNumber + "】");
            TimeUnit.SECONDS.sleep(4);
        } catch (Exception e) {
            e.printStackTrace();
            result = "订单【" + orderNumber + "】支付失败：" + e.getMessage();
        } finally {
            logger.info("结束支付订单【" + orderNumber + "】");
        }
        return result;
    }

    /**
     * 使用 Redisson 加锁 · 可重入锁
     * <p>
     * 支付：http://localhost:8080/redisson/payment2?orderNumber=885867878
     *
     * @param orderNumber ：订单号
     * @return
     */
    @GetMapping("redisson/payment2")
    public String payment(@RequestParam Integer orderNumber) {
        String result = "订单【" + orderNumber + "】支付成功!";
        logger.info("用户请求支付订单【" + orderNumber + "】");

        String key = "com.wmx.wmxredis.controller.RedissonController.payment_" + orderNumber;
        /**
         * getLock(String name)：按名称返回锁实例，实现了一个非公平的可重入锁，因此不能保证线程获得顺序
         * lock():获取锁，如果锁不可用，则当前线程将处于休眠状态，直到获得锁为止
         */
        RLock lock = redissonClient.getLock(key);
        lock.lock();
        try {
            logger.info("查询支付状态.");
            TimeUnit.SECONDS.sleep(4);
            logger.info("开始支付订单【" + orderNumber + "】");
            TimeUnit.SECONDS.sleep(4);
        } catch (Exception e) {
            e.printStackTrace();
            result = "订单【" + orderNumber + "】支付失败：" + e.getMessage();
        } finally {
            logger.info("结束支付订单【" + orderNumber + "】");
            /**
             * unlock()：释放锁， Lock 接口的实现类通常会对线程释放锁（通常只有锁的持有者才能释放锁）施加限制，
             * 如果违反了限制，则可能会抛出（未检查的）异常。
             */
            lock.unlock();
        }
        return result;
    }
}
