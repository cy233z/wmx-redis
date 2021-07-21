package com.wmx.wmxredis.controller;

import com.google.common.base.Stopwatch;
import com.wmx.wmxredis.beans.Person;
import com.wmx.wmxredis.resultAPI.ResultCode;
import com.wmx.wmxredis.resultAPI.ResultData;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * RedisTemplate 操作 redis
 *
 * @author wangMaoXiong
 */
@RestController
@SuppressWarnings("all")
public class RedisController {

    private static final Logger log = LoggerFactory.getLogger(RedisController.class);
    /**
     * 从容器中获取 RedisTemplate 实例
     */
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 保存数据，设置缓存：http://localhost:8080/redis/save?id=1000&name=张三
     *
     * @param person
     * @return
     */
    @GetMapping("redis/save")
    public String redisCache(Person person) {
        ValueOperations opsForValue = redisTemplate.opsForValue();
        ListOperations opsForList = redisTemplate.opsForList();
        HashOperations opsForHash = redisTemplate.opsForHash();

        person.setBirthday(new Date());

        //设置缓存。演示三种数据类型：字符串、列表、hash
        opsForValue.set(RedisController.class.getName() + "_string" + person.getId(), person);
        opsForList.rightPushAll(RedisController.class.getName() + "_list" + person.getId(), person, person);
        opsForHash.put(RedisController.class.getName() + "_map", "person" + person.getId(), person);

        //设置 key 失效时间
        redisTemplate.expire(RedisController.class.getName() + "_string" + person.getId(), 60, TimeUnit.SECONDS);
        redisTemplate.expire(RedisController.class.getName() + "_list" + person.getId(), 60, TimeUnit.SECONDS);
        redisTemplate.expire(RedisController.class.getName() + "_map", 60, TimeUnit.SECONDS);
        return "缓存成功.";
    }

    /**
     * 查询缓存：http://localhost:8080/redis/get?personId=1000
     *
     * @param personId
     * @return
     */
    @GetMapping("redis/get")
    public List<Person> getRedisCache(@RequestParam Integer personId) {
        //1、演示三种数据类型：字符串、列表、hash
        ValueOperations opsForValue = redisTemplate.opsForValue();
        ListOperations opsForList = redisTemplate.opsForList();
        HashOperations opsForHash = redisTemplate.opsForHash();

        //2、读取缓存，如果 key 不存在，则返回为 null.
        Person person = (Person) opsForValue.get(RedisController.class.getName() + "_string" + personId);
        List<Person> personList = opsForList.range(RedisController.class.getName() + "_list" + personId, 0, -1);
        Person person1 = (Person) opsForHash.get(RedisController.class.getName() + "_map", "person" + personId);
        System.out.println("person= " + person);
        System.out.println("personList= " + personList);
        System.out.println("person1= " + person1);
        return personList;
    }

    /**
     * 根据缓存的 key 查询缓存的值，用于查看缓存的值是否正确,因为 key 含有特殊字符 # ，所以采用 POST 请求.
     * http://localhost:8080/redis/getValueByKey
     * <p>
     * 1、DataType type(K key)：查询缓存 key 的类型，DataType 是一个枚举，code(name) 可选值如下：
     * 2、NONE("none"), STRING("string"), LIST("list"), SET("set"), ZSET("zset"), HASH("hash");
     * 3、none 表示 key 不存在，或者类型不确定
     *
     * @param key
     * @return
     */
    @RequestMapping(value = "/redis/getValueByKey", method = RequestMethod.POST)
    public ResultData getValueByKey(@RequestBody String key) {
        Stopwatch stopwatch = null;
        try {
            stopwatch = Stopwatch.createStarted();//秒表
            log.info("根据 key 查询 redis 缓存值， kye={}", key);
            if (ObjectUtils.isEmpty(key)) {
                return new ResultData<Object>(ResultCode.FAIL, "kye 参数错误！");
            }
            boolean hasKey = redisTemplate.hasKey(key);
            if (!hasKey) {
                return new ResultData<Object>(ResultCode.FAIL, "kye 不存在：" + key);
            }
            DataType dataType = redisTemplate.type(key);
            Object value = null;
            if (StringUtils.equalsAnyIgnoreCase("string", dataType.name())) {
                value = redisTemplate.opsForValue().get(key);
            } else if (StringUtils.equalsAnyIgnoreCase("list", dataType.name())) {
                value = redisTemplate.opsForList().range(key, 0, -1);
            } else if (StringUtils.equalsAnyIgnoreCase("set", dataType.name())) {
                value = redisTemplate.opsForSet().members(key);
            } else if (StringUtils.equalsAnyIgnoreCase("zset", dataType.name())) {
                value = redisTemplate.opsForZSet().range(key, 0, -1);
            } else if (StringUtils.equalsAnyIgnoreCase("hash", dataType.name())) {
                value = redisTemplate.opsForHash().entries(key);
            } else {
                return new ResultData<Object>(ResultCode.FAIL, "kye 的类型错误：" + key);
            }
            long elapsed = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
            return new ResultData<Object>(ResultCode.SUCCESS.getCode(), "查询成功！,耗时：" + elapsed + "(毫秒)", value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResultData<Object>(ResultCode.FAIL, e.getMessage());
        } finally {
            if (stopwatch != null && stopwatch.isRunning()) {
                stopwatch.stop();
            }
        }
    }
}

