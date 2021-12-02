package com.wmx.wmxredis.jedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.*;

/**
 * Jedis 客户端 API 练习——操作 Redis Map类型
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2021/11/29 18:39
 */
@RestController
public class JedisHashController {

    private static final Logger LOG = LoggerFactory.getLogger(JedisHashController.class);

    /**
     * 线程安全的 Redis 连接工厂，{@link RedisAutoConfiguration} 启动已经添加到了容器中，可以直接获取使用
     * RedisConnectionFactory RedisTemplate.getConnectionFactory(): 也可以获取 RedisConnectionFactory 连接工厂
     */
    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * http://localhost:8080/jedis/hashMap
     * 为 map 中指定字段设置值，如果该字段已经存在，并且HSET刚刚更新了该值，则返回0，否则如果创建了新字段，则返回1
     * Long hset(final String key, final String field, final String value)
     * String hget(final String key, final String field)：获取map中指定字段的值，如果未找到字段或键不存在，则返回null.
     * <p>
     * String hmset(final String key, final Map<String, String> hash)：将map中的各个字段设置为各个新值，如果 key 不存在，则自动新建
     * 获取 map 中指定字段的值，返回值是一个与请求顺序相同的列表，不存在的字段返回 null，key 不存在时，当作空 map，返回的列表元素全部为 null.
     * List<String> hmget(final String key, final String... fields)
     * <p>
     * Set<String> hkeys(final String key)：获取map中所有的 key
     * List<String> hvals(final String key)：获取map中所有的 value
     * Map<String, String> hgetAll(final String key)：获取整个 map 对象值
     * <p>
     * Long hdel(final String key, final String... fields)：删除map中指定的元素，返回删除成功的个数
     * Long hlen(final String key)：获取 map 中元素的个数，key 不存在时，返回0.
     * Boolean hexists(final String key, final String field)：如果map中包含指定字段，则返回true，如果找不到key或字段不存在，则返回false
     * <p>
     * 将 map 中指定字段的值(必须是数字)加上 value 值，若key不存在，则创建一个新值，如果字段不存在或包含字符串，则在应用该操作之前，该值将设置为0。
     * 由于 value 参数是有符号的，所以可以使用此命令执行递增和递减。
     * Long hincrBy(final String key, final String field, final long value)
     *
     * @return
     */
    @GetMapping("/jedis/hashMap")
    public Map<String, Object> hashMap() {
        Map<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("code", 200);
        resultMap.put("msg", "success");

        JedisConnection jedisConnection = null;
        try {
            jedisConnection = (JedisConnection) RedisConnectionUtils.getConnection(redisConnectionFactory, true);
            Jedis jedis = jedisConnection.getNativeConnection();

            Map<String, String> dataMap = new HashMap<>(16);
            dataMap.put("name", "许仙");
            dataMap.put("age", "25");
            dataMap.put("sex", "男");
            dataMap.put("address", "深圳市");
            dataMap.put("idCard", "4325261521845211124");

            //hmset=OK
            String hmset = jedis.hmset("jedisHashMap", dataMap);
            System.out.println("hmset=" + hmset);

            //hmget=[许仙, null, 25]
            List<String> hmget = jedis.hmget("jedisHashMap", "name", "id", "age");
            System.out.println("hmget=" + hmget);

            //删除map中的某个键值
            Long hdel = jedis.hdel("jedisHashMap", "sex");
            System.out.println("hdel=" + hdel);

            //获取 map 中的元素个数
            Long hlen = jedis.hlen("jedisHashMap");
            System.out.println("hlen=" + hlen);

            //判断map中是否存在指定字段
            Boolean hexists = jedis.hexists("jedisHashMap", "name");
            System.out.println("hexists=" + hexists);

            //返回map对象中所有的key
            Set<String> hkeys = jedis.hkeys("jedisHashMap");
            System.out.println("hkeys=" + hkeys);

            //返回map对象中所有的value
            List<String> hvals = jedis.hvals("jedisHashMap");
            System.out.println("hvals=" + hvals);

            //逐个遍历
            Iterator<String> iter = jedis.hkeys("jedisHashMap").iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                System.out.println(key + "：" + jedis.hmget("jedisHashMap", key));
            }

            //添加数据
            Long hset = jedis.hset("jedisHashMap", "education", "大学本科");
            System.out.println("hset=" + hset);

            //获取指定的值
            String hget = jedis.hget("jedisHashMap", "education");
            System.out.println("hget=" + hget);

            // 为key中的域 field 的值加上增量 increment
            Long hincrBy = jedis.hincrBy("jedisHashMap", "age", 20);
            System.out.println("hincrBy=" + hincrBy);

            //获取整个map对象
            Map<String, String> hgetAll = jedis.hgetAll("jedisHashMap");
            System.out.println("hgetAll=" + hgetAll);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            getErrrMsg(resultMap, e);
        } finally {
            if (jedisConnection != null) {
                RedisConnectionUtils.releaseConnection(jedisConnection, redisConnectionFactory);
            }
        }
        return resultMap;
    }


    private void getErrrMsg(Map<String, Object> resultMap, Exception e) {
        resultMap.put("code", 500);
        resultMap.put("msg", e.getMessage());
        resultMap.put("data", null);
    }
}
