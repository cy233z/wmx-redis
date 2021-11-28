package com.wmx.wmxredis.jedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
     * http://localhost:8080/jedis/getJedis
     *
     * @return
     */
    @GetMapping("/jedis/getJedis")
    public Map<String, Object> getJedis() {
        Map<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("code", 200);

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

            Set<String> keys = jedis.keys("*");
            resultMap.put("data", keys);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            resultMap.put("code", 500);
            resultMap.put("msg", e.getMessage());
            resultMap.put("data", null);
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

}
