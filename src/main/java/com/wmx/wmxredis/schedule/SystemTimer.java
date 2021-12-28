package com.wmx.wmxredis.schedule;


import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

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
     * 每隔约定时间执行一次。上一次任务执行完成如果没有超时，则会继续延迟，如果已经超时，下一次任务则会立即执行
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


}