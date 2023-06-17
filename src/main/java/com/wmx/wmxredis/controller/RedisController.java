package com.wmx.wmxredis.controller;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.wmx.wmxredis.beans.Person;
import com.wmx.wmxredis.resultAPI.ResultCode;
import com.wmx.wmxredis.resultAPI.ResultData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * RedisTemplate 操作 redis
 *
 * @author wangMaoXiong
 */
@RestController
public class RedisController {

    private static final Logger log = LoggerFactory.getLogger(RedisController.class);
    /**
     * 从容器中获取 RedisTemplate 实例
     */
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 保存数据，设置缓存：http://localhost:8080/redis/save?id=1000&name=张三
     * <p>
     * 业务方法未加 Spring @Transactional 事务注解，方法发生异常时，RedisTemplate 的增、删、改等操作不会回滚。
     *
     * @param person
     * @return
     */
    @GetMapping("redis/save")
    public ResultData<Object> redisCache(Person person) {
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
        return new ResultData("缓存成功");
    }

    /**
     * http://localhost:8080/redis/setValue?key=1&value=张三
     * <p>
     * 业务方法无论有没有加 Spring @Transactional 事务注解，方法发生异常时，
     * 默认情况下 RedisTemplate 的增、删、改等操作都不会回滚，RedisTemplate API 一执行，redis 数据库中就会立马生效。
     *
     * @param key
     * @param value
     * @return
     */
    @GetMapping("redis/setValue")
    @Transactional(rollbackFor = Exception.class)
    public ResultData<Object> setValue(@RequestParam String key,
                                       @RequestParam String value) {
        redisTemplate.opsForValue().set(key, value, 60, TimeUnit.SECONDS);
        // 故意发生异常（上面的方法执行完成，redis 中就会看到数据，即使发生异常，redis 数据也不会回滚）
        System.out.println(Integer.parseInt(key));
        Long setValueCount = redisTemplate.opsForValue().increment("setValue_count", 1);
        return new ResultData(setValueCount);
    }

    /**
     * http://localhost:8080/redis/setValue?key=1&value=张三
     * <p>
     * RedisTemplate 使用 setEnableTransactionSupport 手动管理事务时，multi() 开启事务后，事务只能提交(exec) 或者丢弃(discard)。
     *
     * @param key
     * @param value
     * @return
     */
    @GetMapping("redis/setValue2")
    @Transactional(rollbackFor = Exception.class)
    public ResultData<Object> setValue2(@RequestParam String key,
                                        @RequestParam String value) {
        Long setValueCount = null;
        try {
            // 设置启用事务支持（默认是false）
            redisTemplate.setEnableTransactionSupport(true);
            // 开启事务
            redisTemplate.multi();

            // 设置缓存
            redisTemplate.opsForValue().set(key, value, 60, TimeUnit.SECONDS);
            // 自增，并返回自增后的结果，key 不存在时，自动新增为1.
            // 因为此时事务还未提交，所以会返回 null.
            setValueCount = redisTemplate.opsForValue().increment("setValue_count", 1);

            // 故意发生异常（上面的方法执行完成，redis 中就会看到数据，即使发生异常，redis 数据也不会回滚）
            System.out.println(Integer.parseInt(key));

            // 提交/执行事务（只要未提交事务，则上面的命令都不会执行），一旦提交事务，则全部命令会立即执行，redis中的数据会立马更新
            // 注意：如果事务已经丢弃(discard)，则不能再提交，否则异常：InvalidDataAccessApiUsageException: No ongoing transaction. Did you forget to call multi?
            redisTemplate.exec();
        } catch (Exception e) {
            // 丢弃事务（所有命令都将丢弃，不会再生效）
            // 如果事务已经提交，则不能在丢弃事务，否则报错：java.lang.IllegalStateException: Connection has no active transaction
            redisTemplate.discard();
            throw e;
        } finally {
            // 关闭事务支持
            // redisTemplate.setEnableTransactionSupport(false);
        }
        return new ResultData(setValueCount);
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

    /**
     * http://localhost:8080/redis/execute?key=wwww&value=1rui
     * <p>
     * Boolean setIfAbsent(K key, V value, long timeout, TimeUnit unit)
     * Boolean setIfAbsent(K key, V value, Duration timeout)
     * * 1、key 不存在时进行设值，返回 true; 否则 key 存在时，不进行设值，返回 false.
     * * 2、此方法相当于先设置 key，然后设置 key 的过期时间，它的操作是原子性的，是事务安全的。
     * * 3、相当于：SET anyLock unique_value NX PX 30000，NX是指如果key不存在就成功，key存在返回false，PX可以指定过期时间
     * T execute(RedisScript<T> script, List<K> keys, Object... args)：执行给定的脚本。
     * * 1、多个操作使用 lau 脚本统一执行是事务安全的，具有原子性
     * * 2、脚本中 KEYS[x] 是对 keys 进去取值，ARGV[x] 是对 args 进行取值，索引从1开始.
     * * 3、返回脚本执行的结果，类型与 RedisScript 的类型一致。
     *
     * @param key
     * @param value
     * @return
     * @throws InterruptedException
     */
    @GetMapping("redis/execute")
    public Map<String, Object> execute(@RequestParam String key, @RequestParam String value) {
        Map<String, Object> returnMap = new HashMap<>(8);
        Boolean ifAbsent = false;
        try {
            ifAbsent = redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(60));
            if (!ifAbsent) {
                returnMap.put("code", 500);
                returnMap.put("msg", "程序正在处理中，请稍后再试！");
                return returnMap;
            }
            //休眠 10 秒，模拟执行业务代码
            TimeUnit.SECONDS.sleep(10);
            System.out.println("执行业务代码.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ifAbsent) {
                // 接口执行完毕后删除 key，key 不存在时 execute 方法返回 0
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                RedisScript<Long> redisScript = RedisScript.of(script, Long.class);
                // 返回删除key的个数，未删除成功时，返回 0
                Object execute = redisTemplate.execute(redisScript, Arrays.asList(key), value);
                returnMap.put("data", execute);
            }
        }
        returnMap.put("code", 200);
        returnMap.put("msg", "seccess");
        return returnMap;
    }

    /**
     * http://localhost:8080/redis/testString2?code=111
     * <p>
     * void set(K key, V value)：字符串类型可以直接存储 List<Map> 对象，但是存储 String[] 数组对象，反序列化时会报错。
     */
    @GetMapping("redis/testString2")
    public List<Map<String, Object>> testString2(String code) {
        System.out.println(code.length());

        List<Map<String, Object>> dataList = Lists.newArrayList();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> dataMap = new HashMap<>(8);
            dataMap.put("fid", UUID.randomUUID().toString());
            dataMap.put("agency_id", UUID.randomUUID().toString());
            dataMap.put("agency_code", UUID.randomUUID().toString());
            dataMap.put("agency_name", "雄哥测试 bgtCommonDAO");
            dataMap.put("mof_div_id", UUID.randomUUID().toString());
            dataMap.put("mof_div_code", UUID.randomUUID().toString());
            dataMap.put("mof_div_name", UUID.randomUUID().toString());
            dataMap.put("version_start", "202-10");
            dataMap.put("version_end", "2021-12");
            dataList.add(dataMap);
        }

        redisTemplate.opsForValue().set("wmx2", dataList);
        List<Map<String, Object>> wmx1 = (List<Map<String, Object>>) redisTemplate.opsForValue().get("wmx2");
        return wmx1;
    }
}

