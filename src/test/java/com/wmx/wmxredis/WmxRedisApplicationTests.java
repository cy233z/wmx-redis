package com.wmx.wmxredis;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WmxRedisApplicationTests {

    //注入 RedisTemplate 或者 StringRedisTemplate 其中一个即可，前者是后者的父类，它们都已经默认在 Spring 容器中了。
    //org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration 中已经自动将 RedisTemplate 添加到了容器中，直接获取使用即可.
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //org.springframework.data.redis.core.ValueOperations 处理 String 类型数据
    @Test
    public void test1() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();//清空 redis 所有缓存的数据。
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set("user.name", "张无忌 ");//添加值
        valueOperations.set("upload.process", "65", 60, TimeUnit.SECONDS);//添加值，失效时间60秒
        valueOperations.set("user.status", "online", Duration.ofSeconds(20));//添加值，失效时间20秒

        String user_name = valueOperations.get("user.name");//取值
        String upload_process = valueOperations.get("upload.process");//取值
        String user_status = valueOperations.get("user.status");//取值
        String user_status2 = valueOperations.get("user.status", 2, 4);//取值，截取子符串
        //输出：张无忌 , 65, online, lin
        System.out.println(user_name + ", " + upload_process + ", " + user_status + ", " + user_status2);

        Integer hello = valueOperations.append("user.hit", "Hello");//为末尾追加值
        String hit1 = valueOperations.get("user.hit");//取值
        Integer hello2 = valueOperations.append("user.hit", " World");//为末尾追加值
        String hit2 = valueOperations.get("user.hit");//取值
        Long size = valueOperations.size("user.hit");//字符串的长度
        //输出：5 - Hello, 11 - Hello World
        System.out.println(hello + " - " + hit1 + ", " + hello2 + " - " + hit2 + ", " + size);

        //key 不存在时，才进行添加
        Boolean aBoolean1 = valueOperations.setIfAbsent("name", "三丰", 60, TimeUnit.SECONDS);
        Boolean aBoolean2 = valueOperations.setIfAbsent("name", "四喜", 60, TimeUnit.SECONDS);
        System.out.println(aBoolean1 + ", " + aBoolean2 + ", " + valueOperations.get("name"));//输出：true, false, 三丰

        //key 存在时，才进行添加
        Boolean age1 = valueOperations.setIfPresent("age", "45", Duration.ofSeconds(60));
        valueOperations.set("age", "35");
        Boolean age2 = valueOperations.setIfPresent("age", "55", Duration.ofSeconds(60));
        System.out.println(age1 + ", " + age2 + ", " + valueOperations.get("age"));//输出：false, true, 55

        valueOperations.set("l1", "1", Duration.ofSeconds(60));
        valueOperations.set("d1", "3.14", Duration.ofSeconds(60));
        valueOperations.set("total", "100", Duration.ofSeconds(60));
        Long count1 = valueOperations.increment("l1");//数值做加法
        Long count2 = valueOperations.increment("l1", 10L);
        Double d1 = valueOperations.increment("d1", 40.00);
        Long total1 = valueOperations.decrement("total");//数值做减法
        Long total2 = valueOperations.decrement("total2", 35);
        System.out.println(count1 + ", " + count2 + ", " + d1 + ", " + total1 + ", " + total2);//输出：2, 12, 43.14, 99, 64

        List<String> keyList = new ArrayList<>();
        keyList.add("l1");
        keyList.add("d1");
        keyList.add("f1");
        List<String> list = valueOperations.multiGet(keyList);//同时获取多个 key 的 value
        System.out.println(list);//[12, 43.140000000000001, null]

        Map<String, String> dataMap = new LinkedHashMap<>();
        dataMap.put("multi_name", "雄霸");
        dataMap.put("multi_age", "44");
        valueOperations.multiSet(dataMap);//同时设置多个key-value
        //输出：[雄霸, 44]
        System.out.println(valueOperations.multiGet(Arrays.asList(new String[]{"multi_name", "multi_age"})));
    }
}