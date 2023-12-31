package com.wmx.wmxredis.redisson;

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
     * RedissonClient.getLock(String name)：可重入锁,按名称返回锁实例，实现了一个非公平的可重入锁，因此不能保证线程获得顺序
     * lock(): 获取锁，如果锁不可用，则当前线程将处于休眠状态，直到获得锁为止
     * <p>
     * 支付：http://localhost:8080/redisson/payment2?orderNumber=885867878
     *
     * @param orderNumber ：订单号
     * @return
     */
    @GetMapping("redisson/payment2")
    public String payment2(@RequestParam Integer orderNumber) {
        String result = "订单【" + orderNumber + "】支付成功!";
        logger.info("用户请求支付订单【" + orderNumber + "】");

        String key = "com.wmx.wmxredis.redisson.RedissonController.payment2_" + orderNumber;
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
             * 注意：如果锁已经被释放了，重复释放时，会抛出异常.
             */
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
        return result;
    }

    /**
     * RedissonClient.getLock(String name)：可重入锁, 实现了一个非公平的可重入锁，因此不能保证线程获得顺序
     * boolean tryLock(long waitTime, long leaseTime, TimeUnit unit)：尝试获取锁
     * * 1、waitTime：获取锁时的等待时间，超时自动放弃，线程不再继续阻塞，方法返回 false
     * * 2、leaseTime：获取到锁后，指定加锁的时间，超时后自动解锁
     * * 3、如果成功获取锁，则返回 true，否则返回 false。
     * boolean tryLock()：仅当锁在调用时可用时才获取锁。
     * * 1、如果锁可用，则获取锁并立即返回值 true.
     * * 2、如果锁不可用，则此方法将立即返回值 false.
     * lock():获取锁，如果锁不可用，则当前线程将处于休眠状态，直到获得锁为止
     * <p>
     * http://localhost:8080/redisson/payment3?orderNumber=8856767
     *
     * @param orderNumber
     * @return
     */
    @GetMapping("redisson/payment3")
    public String payment3(@RequestParam Integer orderNumber) {
        String result = "订单【" + orderNumber + "】支付成功.";
        logger.info("用户请求支付订单【" + orderNumber + "】.");

        String key = "com.wmx.wmxredis.redisson.RedissonController.payment3_" + orderNumber;
        RLock lock = redissonClient.getLock(key);
        boolean tryLock = false;
        try {
            tryLock = lock.tryLock(30, 180, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!tryLock) {
            return "订单【" + orderNumber + "】正在支付中，请耐心等待！";
        }
        try {
            logger.info("查询支付状态");
            TimeUnit.SECONDS.sleep(40);
            logger.info("开始支付订单【" + orderNumber + "】");
            TimeUnit.SECONDS.sleep(40);
        } catch (Exception e) {
            e.printStackTrace();
            result = "订单【" + orderNumber + "】支付失败：" + e.getMessage();
        } finally {
            logger.info("结束支付订单【" + orderNumber + "】");
            /**
             * boolean isLocked():检查锁是否被任何线程锁定，被锁定时返回 true，否则返回 false.
             * unlock()：释放锁， Lock 接口的实现类通常会对线程释放锁（通常只有锁的持有者才能释放锁）施加限制，
             * 如果违反了限制，则可能会抛出（未检查的）异常。如果锁已经被释放，重复释放时，会抛出异常。
             */
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
        return result;
    }


}
