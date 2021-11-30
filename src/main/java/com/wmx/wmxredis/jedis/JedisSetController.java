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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Jedis 客户端 API 练习——操作 Redis 集合类型
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2021/11/29 18:39
 */
@RestController
public class JedisSetController {

    private static final Logger LOG = LoggerFactory.getLogger(JedisSetController.class);

    /**
     * 线程安全的 Redis 连接工厂，{@link RedisAutoConfiguration} 启动已经添加到了容器中，可以直接获取使用
     * RedisConnectionFactory RedisTemplate.getConnectionFactory(): 也可以获取 RedisConnectionFactory 连接工厂
     */
    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * http://localhost:8080/jedis/set
     * <p>
     * 将元素添加到集合中，如果元素已经存在，则不执行任何操作，如果key不存在，则将创建一个新的集合，然后添加。添加成功返回1，否则返回0
     * Long sadd(final String key, final String... members)
     * <p>
     * 获取集合中的所有成员(元素)，key 不存在时返回空集合。
     * Set<String> smembers(final String key)
     * <p>
     * 从集合中删除指定的成员，如果元素在集合中不存在，则不执行任何操作。返回成功删除的个数.
     * Long srem(final String key, final String... members)
     * <p>
     * Boolean sismember(final String key, final String member)：判断集合是否包含指定元素，存在时返回 true；不存在或key不存在，则为false。
     * Long scard(final String key)：获取集合中元素的个数，key 不存在时返回0.
     * <p>
     * 从集合中随机移出元素，如果集合为空或键不存在，则返回 null 对象
     * String spop(final String key)
     * Set<String> spop(final String key, final long count)：
     *
     * @return
     */
    @GetMapping("/jedis/set")
    public Map<String, Object> set() {
        Map<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("code", 200);
        resultMap.put("msg", "success");

        JedisConnection jedisConnection = null;
        try {
            jedisConnection = (JedisConnection) RedisConnectionUtils.getConnection(redisConnectionFactory, true);

            // 有了 Jedis 则可以使用它的任意方法操作 Redis 了
            Jedis jedis = jedisConnection.getNativeConnection();

            //添加元素
            jedis.sadd("jedisSet", "1");
            jedis.sadd("jedisSet", "2");
            jedis.sadd("jedisSet", "3");
            jedis.sadd("jedisSet", "4");
            jedis.sadd("jedisSet", "5");
            jedis.sadd("jedisSet", "Hi");
            jedis.sadd("jedisSet", "中国");
            //查询全部元素
            Set<String> smembers = jedis.smembers("jedisSet");
            System.out.println("smembers=" + smembers);

            // 删除元素
            Long srem = jedis.srem("jedisSet", "1", "21");
            System.out.println("srem=" + srem);

            //判断元素是否存在
            Boolean sismember = jedis.sismember("jedisSet", "2");
            System.out.println("sismember=" + sismember);

            // 获取元素的个数
            Long scard = jedis.scard("jedisSet");
            System.out.println("scard=" + scard);

            // 随机出栈
            Set<String> spop = jedis.spop("jedisSet", 2);
            System.out.println("spop=" + spop);

            jedis.sadd("sets1", "HashSet1");
            jedis.sadd("sets1", "SortedSet1");
            jedis.sadd("sets1", "TreeSet");
            jedis.sadd("sets2", "HashSet2");
            jedis.sadd("sets2", "SortedSet1");
            jedis.sadd("sets2", "TreeSet1");
            // 交集
            System.out.println(jedis.sinter("sets1", "sets2"));
            // 并集
            System.out.println(jedis.sunion("sets1", "sets2"));
            // 差集
            System.out.println(jedis.sdiff("sets1", "sets2"));

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
