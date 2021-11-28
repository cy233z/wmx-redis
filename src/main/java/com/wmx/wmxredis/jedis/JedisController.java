package com.wmx.wmxredis.jedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Jedis 客户端 API 练习——操作 Redis 数据库
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2021/11/28 9:38
 */
@RestController
public class JedisController {

    private static final Logger LOG = LoggerFactory.getLogger(JedisController.class);
    /**
     * 从容器中获取 RedisTemplate 实例
     * org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration 中
     * 已经自动将 RedisTemplate 添加到了容器中，直接获取使用即可.
     */
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 线程安全的 Redis 连接工厂，{@link RedisAutoConfiguration} 启动已经添加到了容器中，可以直接获取使用
     * RedisConnectionFactory RedisTemplate.getConnectionFactory(): 也可以获取 RedisConnectionFactory 连接工厂
     */
    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 保存、设置字符串值
     * http://localhost:8080/jedis/setString?key=jedis&value=你好
     * http://localhost:8080/jedis/setString?key=jedis&value=Hi&isAppend=1
     * <p>
     * 为 key 设置字符串值(key存在时覆盖), 字符串值长度不能超过1073741824字节(1 GB)，返回状态代码回复：
     * String set(final String key, final String value)
     * String set(final String key, final String value, final String nxxx, final String expx,final long time)
     * String set(final byte[] key, final byte[] value, final byte[] nxxx, final byte[] expx,final long time)
     * * nxxx：NX|XX，NX——仅当密钥不存在时才设置该密钥。XX——仅当密钥已存在时才设置该密钥
     * * expx：EX|PX，过期时间单位：EX=秒；expPX=毫秒
     * * time：以 expx 为单位的过期时间
     * <p>
     * 如果键已经存在并且是字符串，则在值的末尾追加提供的新值。如果键不存在，它将被创建。返回追加操作后字符串的总长度。
     * Long append(final byte[] key, final byte[] value)
     * Long append(final String key, final String value)
     *
     * @param key      ：key
     * @param value    ：value
     * @param isAppend ：是否追加内容, key 不存在时自动创建
     * @return
     */
    @GetMapping("/jedis/setString")
    public Map<String, Object> setStringValue(@RequestParam String key, @RequestParam String value, Integer isAppend) {
        Map<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("code", 200);
        resultMap.put("msg", "success");

        JedisConnection jedisConnection = null;
        try {
            /**
             * RedisConnectionFactory getRequiredConnectionFactory()：获取 ConnectionFactory 连接工厂，未设置时，抛异常.
             * RedisConnection getConnection(RedisConnectionFactory factory): 从工厂获取 Redis 连接
             * * 1、返回绑定到当前线程的任何现有对应连接，例如在使用事务管理器时，否则将始终创建新连接。
             * * 2、factory：用于创建连接的参数工厂连接工厂
             * * 3、返回没有事务管理的活动 Redis 连接
             * RedisConnection getConnection(RedisConnectionFactory factory, boolean enableTranactionSupport)：从工厂获取 Redis 连接
             * * 1、enableTranactionSupport 如果为 true，则返回带有事务管理的活动 Redis 连接
             */
            jedisConnection = (JedisConnection) RedisConnectionUtils.getConnection(redisConnectionFactory, true);

            // 有了 Jedis 则可以使用它的任意方法操作 Redis 了
            Jedis jedis = jedisConnection.getNativeConnection();
            if (isAppend != null && isAppend.equals(1)) {
                jedis.append(key, value);
            } else {
                jedis.set(key, value);
            }
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
     * http://localhost:8080/jedis/setString
     * body：{"jedis1":"001001","jedis2":1993}
     * <p>
     * 同时设置多个键值对，格式：key1,value1,key2,value2,key3,value3...。
     * MSET 和 MSETNX 都是原子操作，MSET 对于 key 存在时会覆盖，MSETNX 对于 key 存在时不做任何覆盖。
     * String mset(final String... keysvalues) ：返回 OK
     * Long msetnx(final String... keysvalues) ：如果所有键都设置成功，则返回为1，否则返回0(即至少有一个key已经存在)
     *
     * @param dataMap
     * @return
     */
    @PostMapping("/jedis/setString")
    public Map<String, Object> setStringValue(@RequestBody Map<String, Object> dataMap) {
        Map<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("code", 200);
        resultMap.put("msg", "success");

        JedisConnection jedisConnection = null;
        try {
            jedisConnection = (JedisConnection) RedisConnectionUtils.getConnection(redisConnectionFactory, true);
            Jedis jedis = jedisConnection.getNativeConnection();

            List<String> keysvalues = new ArrayList<>(dataMap.size() * 2);
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                keysvalues.add(entry.getKey());
                keysvalues.add(String.valueOf(entry.getValue()));
            }
            if (keysvalues.size() > 0) {
                String mset = jedis.mset(keysvalues.toArray(new String[keysvalues.size()]));
                resultMap.put("data", mset);
            }
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
     * http://localhost:8080/jedis/delKeys?keys=jedis,hi
     * 删除指定的 key, 如果给定的 key 不存在，则不会执行任何操作，返回已删除的键数
     * Long del(final String key)
     * Long del(final String... keys)
     * Long del(final byte[] key)
     * Long del(final byte[]... keys)
     *
     * @param keys ：用逗号分隔，如 a,b,c
     * @return
     */
    @GetMapping("/jedis/delKeys")
    public Map<String, Object> delKeys(@RequestParam String keys) {
        Map<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("code", 200);
        resultMap.put("msg", "success");

        JedisConnection jedisConnection = null;
        try {
            jedisConnection = (JedisConnection) RedisConnectionUtils.getConnection(redisConnectionFactory, true);

            Jedis jedis = jedisConnection.getNativeConnection();
            String[] split = keys.split(",");
            Long del = jedis.del(split);
            resultMap.put("data", del);
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
