package com.wmx.wmxredis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Redis 5种数据类型测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    //注入 RedisTemplate 或者 StringRedisTemplate 其中一个即可，前者是后者的父类。
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //org.springframework.data.redis.core.ValueOperations 处理 String 类型数据
    @Test
    public void test1() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();//清空当前连接的 redis 数据库中所有缓存的数据。
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

    //org.springframework.data.redis.core.ListOperations 处理 List 类型数据
    @Test
    public void test2() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();//清空当前连接的 redis 数据库中所有缓存的数据。
        ListOperations<String, String> opsForList = stringRedisTemplate.opsForList();
        Long aLong1 = opsForList.leftPush("list_1", "USA");//将值添加到 list 头部
        Long aLong2 = opsForList.leftPush("list_1", "China");//将值添加到 list 头部
        Long aLong3 = opsForList.leftPushAll("list_1", new String[]{"Java", "C++", "USA", "WPF"});//将多个值添加到 list 头部
        Long size = opsForList.size("list_1");//获取 list 大小
        Long aLong4 = opsForList.leftPush("list_1", "Java", "C#");//将值添加到 list 头部
        Long aLong5 = opsForList.rightPush("list_1", "USA", "Tomcat");//将值添加到 list 头部
        //输出：1, 2, 6, 7, 8, 6
        System.out.println(aLong1 + ", " + aLong2 + ", " + aLong3 + ", " + aLong4 + ", " + aLong5 + ", " + size);
        System.out.println(opsForList.range("list_1", 0, -1));//输出：[WPF, USA, Tomcat, C++, C#, Java, China, USA]

        Long aLong = opsForList.leftPushIfPresent("list_2", "Apple");//当 key 存在时，才进行添加
        System.out.println(aLong + ", " + aLong5);//0, 8

        opsForList.rightPush("list_2", "2019");//将值添加到 list 尾部
        opsForList.rightPush("list_2", "2018");
        opsForList.rightPush("list_2", "2017");
        opsForList.set("list_2", 1, "2008");
        System.out.println(opsForList.range("list_2", 0, -1));//获取指定索引范围的值，[2019, 2008, 2017]

        //同时将多个值添加到右侧
        opsForList.rightPushAll("list_3", new String[]{"mysql", "oracle", "sql server", "redis", "oracle", "sql server", "redis", "oracle"});
        System.out.println(opsForList.range("list_3", 0, -1));//输出：[mysql, oracle, sql server, redis, oracle, sql server, redis, oracle]
        //删除 list 中所有的 "sql server" 值
        Long remove1 = opsForList.remove("list_3", 0, "sql server");//2 - [mysql, oracle, redis, oracle, redis, oracle]
        System.out.println(remove1 + " - " + opsForList.range("list_3", 0, -1));
        //从右往左删除第一次出现的 redis 值
        Long remove2 = opsForList.remove("list_3", -1, "redis");//1 - [mysql, oracle, redis, oracle, oracle]
        System.out.println(remove2 + " - " + opsForList.range("list_3", 0, -1));
        //从左往右删除第一次、第二次出现的 oracle 值
        Long remove3 = opsForList.remove("list_3", 2, "oracle");//2 - [mysql, redis, oracle]
        System.out.println(remove3 + " - " + opsForList.range("list_3", 0, -1));

        opsForList.rightPushAll("list_4", new String[]{"mysql", "oracle", "redis", "DB2"});
        String index1 = opsForList.index("list_4", -1);//查询倒数第一个元素
        String leftPop1 = opsForList.leftPop("list_4");//弹出头部第一个元素
        String rightPop1 = opsForList.rightPop("list_4");//弹出尾部第一个元素
        System.out.println(index1 + ", " + leftPop1 + ", " + rightPop1);//DB2, mysql, DB2

        opsForList.rightPushAll("list_5", new String[]{"Python", "C#", "Java"});
        for (int i = 0; i < 4; i++) {
            //弹出头部元素，当超过60秒还没有元素时，抛出异常 RedisCommandTimeoutException
            //System.out.println(opsForList.leftPop("list_5", 60, TimeUnit.SECONDS));//可以手动通过 redis 客户添加元素：lpush list_5 wmx
        }
    }

    //org.springframework.data.redis.core.HashOperations 处理 hash 数据类型
    @Test
    public void test3() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();//清空当前连接的 redis 数据库中所有缓存的数据。

        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
        Long delete = opsForHash.delete("hash_1", "name", "age");//删除指定元素
        Boolean hasKey = opsForHash.hasKey("hash_1", "name");//判断是否含有指定的元素
        Object name1 = opsForHash.get("hash_1", "name");//获取指定的元素
        Set<Object> keys = opsForHash.keys("hash_1");//获取所有的 key
        Long hash_1 = opsForHash.size("hash_1");//获取 hash 的大小
        System.out.println(delete + ", " + hasKey + ", " + name1 + ", " + keys + ", " + hash_1);//输出：0, false, null, [], 0

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("age", "33");
        dataMap.put("name", "Li Si");
        dataMap.put("address", "人民东路45号");
        opsForHash.put("hash_1", "name", "Zan San");//为 hash 添加单个元素
        opsForHash.putAll("hash_1", dataMap);//为 hash 添加多个元素
        opsForHash.putIfAbsent("hash_2", "name", "Chang Sha");//当 key 或者 hashKey 不存在时，才插入值

        List<Object> list = opsForHash.values("hash_1");//获取所有的 values
        Map<Object, Object> entries = opsForHash.entries("hash_1");//获取整个 hash
        Long lengthOfValue1 = opsForHash.lengthOfValue("hash_1", "name");//获取元素的长度
        Long lengthOfValue2 = opsForHash.lengthOfValue("hash_1", "age");
        List<Object> multiGet = opsForHash.multiGet("hash_1", Arrays.asList(new String[]{"name", "address", "birthday"}));//同时获取多个元素值

        //[Li Si, 人民东路45号, 33], {name=Li Si, address=人民东路45号, age=33}, 5, 2, [Li Si, 人民东路45号, null]
        System.out.println(list + ", " + entries + ", " + lengthOfValue1 + ", " + lengthOfValue2 + ", " + multiGet);

        Long increment_age1 = opsForHash.increment("hash_1", "age", 20);//数值元素做加法
        Double increment_age2 = opsForHash.increment("hash_1", "age", 20.45D);
        System.out.println(increment_age1 + ", " + increment_age2);//53, 73.45
    }

    /**
     * org.springframework.data.redis.core.SetOperations 处理 Set 类型数据：
     * Long remove(K key, Object... values)：移除 set 中的元素。返回 set 中剩余的元素个数。
     * * 1、set 中没有元素时，自动删除 key。
     * * 2、key 不存在时，不影响。
     * * 3、values 必须有值，不能为null或者空，否则报错：JedisDataException: ERR wrong number of arguments for 'srem' command
     */
    @Test
    public void test4() {
        stringRedisTemplate.getConnectionFactory().getConnection().flushDb();//清空当前连接的 redis 数据库中所有缓存的数据。
        SetOperations<String, String> opsForSet = stringRedisTemplate.opsForSet();
        Long add = opsForSet.add("set_1", "C", "Java", "Python");//往集合添加元素
        Long remove = opsForSet.remove("set_1", "C");//删除元素
        Long remove2 = opsForSet.remove("set_2", "C");//删除元素
        String set_1 = opsForSet.pop("set_1");//随机弹出 set 中的一个元素
        System.out.println(add + ", " + remove + ", " + remove2 + ", " + set_1);//3, 1, 0, Java

        opsForSet.add("set_2", "华山", "泰山", "衡山");
        Set<String> set_2 = opsForSet.members("set_2");//查询 set 中的所有元素
        opsForSet.move("set_1", "Java", "set_2");
        System.out.println(set_2);//[泰山, 衡山, 华山]
        Long size = opsForSet.size("set_2");//获取 set 的大小
        System.out.println(opsForSet.members("set_2") + ", " + size);//[泰山, 衡山, 华山], 3

        Boolean member1 = opsForSet.isMember("set_2", "泰山");//判断 set 中是否含义指定的元素值
        Boolean member2 = opsForSet.isMember("set_2", "嵩山");
        System.out.println(member1 + ", " + member2);//true, false

        opsForSet.add("set_3", "Oracle", "Mysql", "DB2", "H2", "Sql Server");
        List<String> set_3 = opsForSet.pop("set_3", 3);
        System.out.println(set_3);//[Oracle, DB2, Mysql]

        opsForSet.add("set_4", "段誉", "乔峰", "虚竹", "王语嫣", "慕容复");
        System.out.println(opsForSet.members("set_4") + ", " + opsForSet.randomMember("set_41"));
        System.out.println(opsForSet.randomMembers("set_4", 3));//随机弹出 set 中指定个数的元素，如随机弹出其中3个元素

        opsForSet.add("set_5_1", "a", "b", "c", "1", "2", "3");
        opsForSet.add("set_5_2", "x", "y", "c", "21", "2", "3");
        Set<String> union1 = opsForSet.union("set_5_1", "set_5_2");//两个集合求并集
        System.out.println(opsForSet.members("set_5_1") + ", " + opsForSet.members("set_5_2"));
        System.out.println(union1);//[3, a, x, c, b, 2, 21, 1, y]

        //多个集合求并集
        Set<String> set_5_1 = opsForSet.union("set_5_1", Arrays.asList(new String[]{"set_5_2", "set_4"}));
        System.out.println(set_5_1);//[王语嫣, 段誉, 21, a, 3, 虚竹, x, b, c, 慕容复, 1, y, 乔峰, 2]

        //多个集合求并集，并将结果存储到新的集合中
        Long aLong = opsForSet.unionAndStore("set_5_1", "set_5_2", "set_5_3");
        System.out.println(aLong + ", " + opsForSet.members("set_5_3"));//9, [3, a, x, c, b, 2, 21, 1, y]

        Set<String> difference = opsForSet.difference("set_5_1", "set_5_2");//求两个集合的差集
        System.out.println(difference);//[a, 1, b]

        Set<String> intersect = opsForSet.intersect("set_5_1", "set_5_2");//求两个集合的交集
        opsForSet.intersectAndStore("set_5_1", "set_5_2", "set_5_3");//当set_5_3集合中有值时，会先被清除
        System.out.println(intersect);//[3, c, 2]
    }

    //org.springframework.data.redis.core.ZSetOperations 处理有序集合(Set) 的数据：
    @Test
    public void test5() {
        stringRedisTemplate.getConnectionFactory().getConnection().flushDb();//清空当前连接的 redis 数据库中所有缓存的数据。
        ZSetOperations<String, String> opsForZSet = stringRedisTemplate.opsForZSet();
        Boolean add1 = opsForZSet.add("zset1", "C++", 3.8);
        Boolean add2 = opsForZSet.add("zset1", "Java", 0.3);
        Boolean add3 = opsForZSet.add("zset1", "Python", 2.5);
        Set<String> zset1 = opsForZSet.range("zset1", 0, -1);
        System.out.println(add1 + ", " + add2 + ", " + add3 + ", " + zset1);//输出：true, true, true, [Java, Python, C++]

        Long rank = opsForZSet.rank("zset1", "C++");
        Long count = opsForZSet.count("zset1", 0.3, 2.5);
        Long size = opsForZSet.size("zset1");
        Double score = opsForZSet.score("zset1", "Java");
        //输出：2, 2, 3, 0.3, [Java, Python, C++]
        System.out.println(rank + ", " + count + ", " + size + ", " + score + ", " +
                opsForZSet.range("zset1", 0, -1));

        opsForZSet.add("zset2", "12", 0);
        opsForZSet.add("zset2", "3c", 1);
        opsForZSet.add("zset2", "4f", 2);
        opsForZSet.add("zset2", "6h", 3);
        opsForZSet.add("zset2", "7p", 4);
        Long remove = opsForZSet.remove("zset2", "4f");
        //Long aLong = opsForZSet.removeRange("zset2", 1, 2);
        Long zset2 = opsForZSet.removeRangeByScore("zset2", 2.0, 3.0);
        System.out.println(opsForZSet.range("zset2", 0, -1));
    }

    /**
     * DataType type(K key)：查看缓存中 key 的类型，DataType 是一个枚举，有如下 code(name) 值：
     * NONE("none"), STRING("string"), LIST("list"), SET("set"), ZSET("zset"), HASH("hash");
     */
    @Test
    public void test() {
        DataType dataType1 = redisTemplate.type("jcxxk2");
        DataType dataType2 = redisTemplate.type("element-server.430000000.elementValueWhere.Sex");

        System.out.println("dataType=========" + dataType1);//dataType=========STRING
        System.out.println("dataType=========" + dataType2);//dataType=========HASH
        System.out.println("code-name====" + dataType1.code() + "," + dataType1.name());//code-name====string,STRING
    }


}
