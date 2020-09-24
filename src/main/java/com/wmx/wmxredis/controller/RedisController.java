package com.wmx.wmxredis.controller;

import com.wmx.wmxredis.beans.Person;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
public class RedisController {
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
        System.out.println("person=" + person);
        System.out.println("personList=" + personList);
        System.out.println("person1=" + person1);
        return personList;
    }
}

