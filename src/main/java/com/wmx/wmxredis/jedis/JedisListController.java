package com.wmx.wmxredis.jedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangMaoXiong
 * @version 1.0
 * @date 2021/11/29 18:39
 */
@RestController
public class JedisListController {

    private static final Logger LOG = LoggerFactory.getLogger(JedisListController.class);

    /**
     * 线程安全的 Redis 连接工厂，{@link RedisAutoConfiguration} 启动已经添加到了容器中，可以直接获取使用
     * RedisConnectionFactory RedisTemplate.getConnectionFactory(): 也可以获取 RedisConnectionFactory 连接工厂
     */
    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 为 list 设置值
     * http://localhost:8080/jedis/setList?key=jedisList&value=你好
     * <p>
     * 将字符串值添加到列表的头部（LPUSH）或尾部（RPUSH），如果键不存在，将在追加操作之前创建一个空列表。如果key存在但不是列表，则返回错误。返回添加后列表中的元素个数。
     * Long rpush(final String key, final String... strings)
     * Long lpush(final String key, final String... strings)
     *
     * @param key   ：键
     * @param value ：值
     * @return
     */
    @GetMapping("/jedis/setList")
    public Map<String, Object> setList(@RequestParam String key, @RequestParam String value) {
        Map<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("code", 200);
        resultMap.put("msg", "success");

        JedisConnection jedisConnection = null;
        try {
            jedisConnection = (JedisConnection) RedisConnectionUtils.getConnection(redisConnectionFactory, true);

            // 有了 Jedis 则可以使用它的任意方法操作 Redis 了
            Jedis jedis = jedisConnection.getNativeConnection();

            Long rpush = jedis.rpush(key, value);
            Long lpush = jedis.lpush(key, value);
            System.out.println("rpush=" + rpush + ", lpush=" + lpush);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            getErrrMsg(resultMap, e);
        } finally {
            /**
             * releaseConnection(@Nullable RedisConnection conn, RedisConnectionFactory factory)：关闭通过给定工厂创建的给定连接（如果未进行外部管理（即未绑定到线程）
             * * conn: 要关闭的 Redis 连接。
             * * factory：用于创建连接的 Redis 工厂
             */
            if (jedisConnection != null) {
                RedisConnectionUtils.releaseConnection(jedisConnection, redisConnectionFactory);
            }
        }
        return resultMap;
    }

    /**
     * 获取 List 中的元素
     * http://localhost:8080/jedis/getList?key=jedisList
     * <p>
     * 获取列表中指定索引范围内的元素，从0开始，-1表示倒数第一个元素，-2表示倒数第二个元素。超出范围的索引不会产生错误。
     * List<String> lrange(final String key, final long start, final long stop)
     * Long llen(final String key)：返回列表的长度，如果key不存在，则返回0（与空列表的行为相同）,如果键处存储的值不是列表，则返回错误。
     * List<String> sort(final String key)：对集合或列表进行排序。默认情况下，只能对数字进行排序，否则报错，元素作为双精度浮点数进行比较。这是最简单的排序形式
     *
     * @param key
     * @return
     */
    @GetMapping("/jedis/getList")
    public Map<String, Object> getList(@RequestParam String key) {
        Map<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("code", 200);
        resultMap.put("msg", "success");

        JedisConnection jedisConnection = null;
        try {
            jedisConnection = (JedisConnection) RedisConnectionUtils.getConnection(redisConnectionFactory, true);

            // 有了 Jedis 则可以使用它的任意方法操作 Redis 了
            Jedis jedis = jedisConnection.getNativeConnection();

            //获取列表的长度
            Long llen = jedis.llen(key);
            System.out.println("列表长度：" + llen);

            //获取指定范围内的元素
            List<String> lrange = jedis.lrange(key, 0, -1);

            resultMap.put("data", lrange);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            getErrrMsg(resultMap, e);
        } finally {
            /**
             * releaseConnection(@Nullable RedisConnection conn, RedisConnectionFactory factory)：关闭通过给定工厂创建的给定连接（如果未进行外部管理（即未绑定到线程）
             * * conn: 要关闭的 Redis 连接。
             * * factory：用于创建连接的 Redis 工厂
             */
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
