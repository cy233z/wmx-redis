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
     * http://localhost:8080/jedis/disorderSet
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
     * <p>
     * Set<String> sinter(final String... keys)：获取所有集合的交集
     * Set<String> sunion(final String... keys)：获取所有集合的并集
     * Set<String> sdiff(final String... keys)：返回存储在key1处的集合与所有集合key2，…，keyN之间的差集
     *
     * @return
     */
    @GetMapping("/jedis/disorderSet")
    public Map<String, Object> disorderSet() {
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

            jedis.sadd("sets1", "华山");
            jedis.sadd("sets1", "泰山");
            jedis.sadd("sets1", "衡山");

            jedis.sadd("sets2", "武当山");
            jedis.sadd("sets2", "少室山");
            jedis.sadd("sets2", "华山");
            // 交集 [华山]
            Set<String> sinter = jedis.sinter("sets1", "sets2");
            System.out.println("sinter=" + sinter);
            // 并集 [华山, 武当山, 衡山, 泰山, 少室山]
            Set<String> sunion = jedis.sunion("sets1", "sets2");
            System.out.println("sunion=" + sunion);
            // 差集 [衡山, 泰山]
            Set<String> sdiff = jedis.sdiff("sets1", "sets2");
            System.out.println("sdiff=" + sdiff);

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

    /**
     * http://localhost:8080/jedis/orderSet
     * <p>
     * 将具有指定权重的元素添加到有序集合中，如果元素存在，则更新权重，并插入到正确的位置以确保排序。
     * 若key不存在，将创建一个新的有序集合，然后添加元素。如果key存在但不是有序集合，则返回错误。
     * 如果添加了新元素，则返回1，如果元素已经存在，且分数已更新，则返回0。权重小的
     * Long zadd(final String key, final double score, final String member)
     * <p>
     * Set<String> zrange(final String key, final long start, final long stop)：获取索范围内的元素，从0开始，-1表示倒数第一个元素。
     * Set<String> zrevrange(final String key, final long start, final long stop)：获取索范围内的元素，并反序输出(不会改变存储的顺序)
     * Long zcard(final String key)：获取集合的元素个数，key 不存在时返回 0
     * <p>
     * Double zscore(final String key, final String member)：获取有序集合中元素的权重，元素/key不存在时，返回 null.
     * Long zrem(final String key, final String... members)：删除有序集合中指定的元素，如果新元素被删除，则返回1；如果新元素不是集合的成员，则返回0
     * Long zcount(final String key, final double min, final double max)：获取指定权重范围[min.max]内的元素个数
     *
     * @return
     */
    @GetMapping("/jedis/orderSet")
    public Map<String, Object> orderSet() {
        Map<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("code", 200);
        resultMap.put("msg", "success");

        JedisConnection jedisConnection = null;
        try {
            jedisConnection = (JedisConnection) RedisConnectionUtils.getConnection(redisConnectionFactory, true);
            Jedis jedis = jedisConnection.getNativeConnection();

            jedis.zadd("orderSet", 200, "华山");
            jedis.zadd("orderSet", 300, "泰山");
            jedis.zadd("orderSet", 250, "衡山");
            jedis.zadd("orderSet", 450, "少室山");
            jedis.zadd("orderSet", 800, "九华山");
            jedis.zadd("orderSet", 100, "黄山");

            // 获取整个集合的元素：[黄山, 华山, 衡山, 泰山, 少室山, 九华山]
            Set<String> zrange = jedis.zrange("orderSet", 0, -1);
            System.out.println("zrange=" + zrange);

            // 获取索范围内的元素，并反序输出：[九华山, 少室山, 泰山, 衡山, 华山, 黄山]
            Set<String> zrevrange = jedis.zrevrange("orderSet", 0, -1);
            System.out.println("zrevrange=" + zrevrange);

            // 获取集合的元素个数
            System.out.println("zcard=" + jedis.zcard("orderSet"));
            // 获取元素权重
            System.out.println("zscore=" + jedis.zscore("orderSet", "少室山"));
            // 删除元素
            System.out.println("zrem=" + jedis.zrem("orderSet", "黄山"));
            // 获取指定权重范围[min.max]内的元素个数
            System.out.println("zcount=" + jedis.zcount("orderSet", 200, 400));

            // 整个集合值 [华山, 衡山, 泰山, 少室山, 九华山]
            System.out.println(jedis.zrange("orderSet", 0, -1));

            //返回排序集中权重介于[min,max]之间的所有元素，具有相同分数的元素按ASCII字符串的字典顺序返回
            //Set<String> zrangeByScore(final String key, final double min, final double max)
            Set<String> zrangeByScore = jedis.zrangeByScore("orderSet", 100, 500);
            System.out.println("zrangeByScore=" + zrangeByScore);

            //返回排序集中权重介于[min,max]之间的元素个数
            //Long zremrangeByScore(final String key, final double min, final double max)
            Long zremrangeByScore = jedis.zremrangeByScore("orderSet", 100, 500);
            System.out.println("zremrangeByScore=" + zremrangeByScore);

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
