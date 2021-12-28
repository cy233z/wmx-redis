package com.wmx.wmxredis.schedule;


import cn.hutool.core.date.DateUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 1、EnableAsync：开启并发执行所有定时任务，这个不是必须的，可以不配置。
 * 2、EnableScheduling：开启定时任务
 * 3、默认情况下一个组件中的所有 @Scheduled 采用一个单线程执行，即一个 @Scheduled 执行完成后，另一个 @Scheduled 才能执行
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2021/12/28 16:57
 */
@Component
@EnableScheduling
@EnableAsync
public class SystemTimer {

    /**
     * 自定义实例名称，程序一启动就随机生成，用于区分不同的实例。
     */
    private String instanceName;

    @PostConstruct
    public void init() {
        instanceName = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        System.out.println("\n==============初始化实例名称：" + instanceName + "\n");
    }

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 每隔 cron 约定时间执行一次。上一次任务执行完成如果没有超时，则会继续延迟，如果已经超时，下一次任务则会立即执行
     * 1、Async ：并发执行此任务，这个不是必须的，可以不配置。
     * 2、开启异步执行时，不再有延迟的问题，因为时间一到，自动会新开线程执行任务.
     * 3、fixedRate、fixedDelay、cron 只能出现一个，不能同时设置。
     *
     * @throws InterruptedException
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    @Async
    public void time1() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            System.out.println("time1->" + Thread.currentThread().getName() + " -> " + dateFormat.format(date));
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 每隔 fixedDelay 毫秒执行一次。下一次会在上一次任务执行完成后，继续延迟 fixedDelay 毫秒后再执行。
     * 1、fixedRate、fixedDelay、cron 只能出现一个，不能同时设置。
     *
     * @throws InterruptedException
     */
    @Scheduled(fixedDelay = 60 * 1000)
    @Async
    public void time2() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            System.out.println("time2->" + Thread.currentThread().getName() + " -> " + dateFormat.format(date));
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 每隔 fixedRate 毫秒执行一次。下一次会在上一次任务执行完成后执行，如果上一次任务执行超时，则下一次会立即执行
     * 1、fixedRate、fixedDelay、cron 只能出现一个，不能同时设置。
     *
     * @throws InterruptedException
     */
    @Scheduled(fixedRate = 60 * 1000)
    @Async
    public void time3() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            System.out.println("time3->" + Thread.currentThread().getName() + " -> " + dateFormat.format(date));
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 集群环境下防止重复执行，同一任务只允许一个实例执行
     * 1、执行任务的线程往 redis 中存储一个 key表示正在执行任务，其它实例则可以不用重复执行。
     * 2、缓存的 key 的过期时间可以根据任务执行的时间间隔来定：
     * * 2.1、比如任务是每天晚上 0 点执行，则过期时间设置为 1 个小时都行，因为第二天又是全新的。
     * * 2.2、间隔执行时，过期时间设置为间隔时间减去方法执行时间，这样即使不同实例不是同一时间启动，也能保证间隔时间内只会有一个实例执行。
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    @Async
    public void time4() {
        try {
            //定时任务执行的间隔时间(毫秒)
            long startTimeMillis = System.currentTimeMillis();
            long intervalTimeMillis = 1 * 60 * 1000;

            String time = DateUtil.date().toString();
            String cacheKey = "SystemTimer#time4";
            Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(cacheKey, instanceName, intervalTimeMillis, TimeUnit.MILLISECONDS);
            if (ifAbsent) {
                System.out.println("==============" + time + " 实例【" + instanceName + "】获得执行任务权限.");
                //可以将任务执行的实例情况存储到 redis 中，最多记录 100条.
                Long leftPush = redisTemplate.opsForList().leftPush(cacheKey + "#log", time + "【" + instanceName + "】");
                if (leftPush > 100) {
                    redisTemplate.opsForList().rightPop(cacheKey + "#log");
                }

                //模拟业务操作耗时.
                TimeUnit.MILLISECONDS.sleep(1000 + new SecureRandom().nextInt(3000));

                //任务执行完成时，防止下一个任务时间到了，上一个任务的 key 还没有过期。重新设置一下 key 的过期时间，因为上面 setIfAbsent 的过期时间并不包括任务执行时间.
                //在减去任务执行耗时的基础上，再多减去几秒，把 expire 操作的耗时也计划在内。如果过期时间低于1秒，就不再设置了。
                long expire = intervalTimeMillis - (System.currentTimeMillis() - startTimeMillis) - 3000;
                if (expire > 1000) {
                    redisTemplate.expire(cacheKey, expire, TimeUnit.MILLISECONDS);
                }
            } else {
                Object value = redisTemplate.opsForValue().get(cacheKey);
                System.out.println("==============" + time + " 实例【" + instanceName + "】未获得执行任务权限，任务当前正在被实例【" + value + "】执行。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}