package com.wmx.wmxredis.redisson;

import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Redssion 操作 redis 5 种基本数据类型，提供了丰富的 API
 * <p>
 * {@link RBucket}：对象处理器，可以操作字符串类型，最大大小支持 512MB，其中所有的操作都提供了同步和异步两种方法
 * {@link RMap}：操作 Map 数据类型，继承自 ConcurrentMap，线程安全，key-value 都不允许为 null。同样有异步方法。
 * {@link RList}：操作 List 列表数据类型
 * {@link RSet}：操作 set 无序集合数据类型
 * {@link RSortedSet}：操作有序集合数据类型
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

    // ========================== String =======================

    /**
     * http://localhost:8080/redssion/hello/string/redssionS
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
    @GetMapping("/string/{key}")
    public Map<String, String> string(@PathVariable String key) throws ExecutionException, InterruptedException {
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

    // ========================== Map =======================

    /**
     * http://localhost:8080/redssion/hello/map/redssionM
     * <p>
     * RMap<K, V> getMap(String name)：通过 key 获取 map 对象，可用于操作 map 数据类型。
     * <p>
     * V put(K key, V value)：往 map 对象中添加元素，持久化到库中.
     * void putAll(java.util.Map<? extends K, ? extends V> map)：往 map 对象中添加元素，持久化到库中.
     * void putAll(Map<? extends K, ? extends V> map, int batchSize)：往 map 对象中添加元素，持久化到库中，指定每个批次添加的元素个数.
     * V putIfAbsent(K key, V value)：map 中的不存在 key 时才进行赋值。
     * <p>
     * boolean expire(long timeToLive, TimeUnit timeUnit): 设置 key 的过期时间，在持久化到库中后再设置过期时间
     * <p>
     * Set<Entry<K, V>> readAllEntrySet()：一次读取 map 中的所有键值对
     * Set<K> readAllKeySet()：一次读取 map 中的所有 key
     * Collection<V> readAllValues()：一次读取 map 中的所有 value
     * Map<K, V> readAllMap(): 以本地实例一次读取所有元素。
     * Set<java.util.Map.Entry<K, V>> entrySet()：获取 Map 中的元素集合，以批次获取，每批次获取10个。
     * V get(Object key)：获取元素
     * Map<K, V> getAll(Set<K> keys)：获取元素
     *
     * @param key
     * @return
     */
    @GetMapping("/map/{key}")
    public Map<String, Object> map(@PathVariable String key) {

        // 获取处理 map 数据类型的对象，往对象中添加元素时，就会持久化到库中
        RMap<String, Object> rMap = redissonClient.getMap("RMap");

        rMap.put(key, key + "-V2");

        Map<String, String> stringMap = MapUtil.builder("id", "P988").put("code", "200").put("name", "张三").map();
        rMap.putAll(stringMap);

        //ifAbsent 不存在，所以1会设置成功，2不会再设置
        rMap.putIfAbsent("ifAbsent", 1);
        rMap.putIfAbsent("ifAbsent", 2);

        //遍历所有元素
        for (Map.Entry<String, Object> entry : rMap.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }

        //设置超时时间，在持久化到库中后在设置过期时间
        boolean expire = rMap.expire(1 * 60, TimeUnit.SECONDS);
        System.out.println("expire=" + expire);

        return rMap;
    }

    // ========================== List =======================

    /**
     * http://localhost:8080/redssion/hello/list/redssionL
     * <p>
     * int addAfter(V elementToFind, V element)：将新元素添加到第一个元素(elementToFind)的后面
     * int addBefore(V elementToFind, V element)：将新元素添加到第一个元素(elementToFind)的前面
     * <p>
     * List<V> readAll()：一次读取所有元素
     *
     * @param key
     * @return
     */
    @GetMapping("/list/{key}")
    public List<Object> list(@PathVariable String key) {
        RList<Object> rList = redissonClient.getList(key);
        rList.add(1);
        rList.addAll(Lists.newArrayList(2, 3, 4, 5, 3, 7, 8, 9));

        rList.addAfter(3, "三");
        rList.addBefore(3, "叁");

        List<Object> readAll = rList.readAll();
        System.out.println("readAll=" + readAll);

        rList.expire(3 * 60, TimeUnit.SECONDS);
        return rList;
    }

    // ========================== Set =======================

    /**
     * http://localhost:8080/redssion/hello/set/RSet
     *
     * @param key
     * @return
     */
    @GetMapping("/set/{key}")
    public Set<Object> set(@PathVariable String key) {
        RSet<Object> rSet = redissonClient.getSet(key);
        rSet.add("a");

        rSet.addAll(Sets.newHashSet("b", "c"));
        rSet.addAll(Sets.newHashSet(2, 34, 5, 67, 35, 78, 87));

        Set<Object> readAll = rSet.readAll();
        System.out.println("readAll=" + readAll);

        rSet.expire(3 * 60, TimeUnit.SECONDS);
        return rSet;
    }

    // ========================== SortedSet =======================

    /**
     * http://localhost:8080/redssion/hello/sortedSet/RSortedSet
     *
     * @param key
     * @return
     */
    @GetMapping("/sortedSet/{key}")
    public Set sortedSet(@PathVariable String key) {
        RSortedSet<Integer> rSortedSet = redissonClient.getSortedSet(key);

        boolean add = rSortedSet.add(88);
        System.out.println(add);

        rSortedSet.addAll(Sets.newHashSet(2, 3, 4, 5, 6, 7, 8, 9));

        Collection<Integer> readAll = rSortedSet.readAll();
        //readAll=[2, 3, 4, 5, 6, 7, 8, 9, 88]
        System.out.println("readAll=" + readAll);

        return rSortedSet;
    }

}
