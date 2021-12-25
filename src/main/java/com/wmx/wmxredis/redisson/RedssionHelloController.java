package com.wmx.wmxredis.redisson;

import cn.hutool.core.map.MapUtil;
import org.redisson.api.RBucket;
import org.redisson.api.RFuture;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Redssion 操作 redis 5 种基本数据类型
 * <p>
 * RBucket：对象处理器，最大大小支持 512MB，其中所有的操作都提供了同步和异步两种方法
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
     * <p>
     * RBucket<V> getBucket(String name)：按名称返回对象持有者实例。
     * <p>
     * 将元素同步持久化到库中,timeToLive：生存时间间隔，timeUnit：时间单位
     * void set(V value)
     * void set(V value, long timeToLive, TimeUnit timeUnit)
     * setAsync：将元素异步持久化到库中,timeToLive：生存时间间隔，timeUnit：时间单位
     * boolean setIfExists：且当key存在时同步持久化到库中,成功设置时返回 true.
     * RFuture<Boolean> setIfExistsAsync(V value): 且当key存在时异步持久化到库中,成功设置时返回 true.
     * <p>
     * long size()：返回对象的大小（字节）
     * long sizeInMemory()：返回Redis内存中对象使用的字节数。
     * boolean touch()：更新对象的上次访问时间。
     * <p>
     * boolean clearExpire()：清除对象(key)的过期时间。成功清除时返回 true，如果对象不存在或没有关联的超时，则返回 false。
     *
     * @param key
     * @return
     */
    @GetMapping("/string/set/{key}")
    public Map<String, String> s1(@PathVariable String key) throws ExecutionException, InterruptedException {
        //获取指定 key 的对象处理器, 字符串类型
        RBucket<String> stringRBucket = redissonClient.getBucket(key);
        //将元素同步持久化到库中.
        stringRBucket.set(key + "-v1", 180, TimeUnit.SECONDS);
        String value1 = stringRBucket.get();

        //异步读写。所有的操作都有对应的异步方法.
        String key2 = key + "_async";
        stringRBucket = redissonClient.getBucket(key2);
        stringRBucket.setAsync(key + "-v2", 180, TimeUnit.SECONDS);
        RFuture<String> rFuture = stringRBucket.getAsync();
        //V get()：阻塞等待结果
        //get(long timeout, TimeUnit unit): 指定阻塞等待的最长时间.如果超时,则 TimeoutException
        //V getNow()：返回结果而不阻塞,如果没有完成,则返回null,可以通过 boolean isDone() 方法检查是否完成.
        //boolean isDone():如果此任务已完成，则返回 true,完成可能是由于正常终止、异常或取消
        //boolean isSuccess():当且仅当I/O操作成功完成时返回 true
        String value2 = rFuture.get();


        boolean clearExpire = stringRBucket.clearExpire();
        System.out.println("clearExpire=" + clearExpire);

        Map<String, String> map = MapUtil.builder(key, value1).put(key2, value2).map();
        return map;
    }

    @GetMapping("/get/{key}")
    public String g1(@PathVariable String key) {
// 设置字符串
        RBucket<String> keyObj = redissonClient.getBucket(key);
        String s = keyObj.get();
        return s;
    }

}
