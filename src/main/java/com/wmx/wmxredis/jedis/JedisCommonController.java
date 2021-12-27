package com.wmx.wmxredis.jedis;

import com.google.common.collect.Lists;
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
 * Jedis 客户端 API 练习——基础通用命令
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2021/12/2 19:22
 */
@RestController
public class JedisCommonController {

    private static final Logger LOG = LoggerFactory.getLogger(JedisStrController.class);

    /**
     * 线程安全的 Redis 连接工厂，{@link RedisAutoConfiguration} 启动已经添加到了容器中，可以直接获取使用
     * RedisConnectionFactory RedisTemplate.getConnectionFactory(): 也可以获取 RedisConnectionFactory 连接工厂
     */
    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * http://localhost:8080/jedis/common
     * <p>
     * Set<String> keys(final String pattern)：返回与全局模式匹配的所有key，如 *、foo*、*foo，'*' 表示任意字符
     * <p>
     * 删除指定的 key, 如果给定的 key 不存在，则不会执行任何操作，返回已删除的键数
     * Long del(final String key)
     * Long del(final String... keys)
     * Long del(final byte[] key)
     * Long del(final byte[]... keys)
     * <p>
     * Long ttl(final String key)：返回key的剩余生存时间（以秒为单位），如果key没有关联的expire，则返回-1;如果key不存在，则返回-2。
     * <p>
     * 以原子方式将 oldkey 重命名为 newkey，如果源和目标名称相同，则返回错误。如果 newkey 已经存在，它将被覆盖。如 oldkey 不存在，则异常。
     * String rename(final String oldkey, final String newkey)
     * <p>
     * 保存、设置字符串值,并指定过期时间，等价于 set(String, String) + expire(String, int)，操作是原子性的。返回状态码
     * String setex(final String key, final int seconds, final String value)
     *
     * @return
     */
    @GetMapping("/jedis/common")
    public Map<String, Object> common() {
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
            Jedis jedis = jedisConnection.getNativeConnection();

            jedis.setex("jedisHi", 60, "哈喽！");

            // keys中传入的可以用通配符
            Set<String> keys = jedis.keys("*");
            System.out.println("keys=" + keys);

            // 返回给定key的过期时间，如果是-1则表示永远有效
            Long sname = jedis.ttl("jedisHi");
            System.out.println("sname=" + sname);

            String rename = jedis.rename("jedisHi", "jedisHi2");
            System.out.println("rename=" + rename);

            //String select(final int index): 选择指定索引的数据库，默认情况下，将自动选择每个新的客户端连接到 DB 0
            //String select = jedis.select(3);
            //System.out.println("select=" + select);

            //Long dbSize():返回当前选定数据库中 key 的个数。
            Long dbSize = jedis.dbSize();
            System.out.println("dbSize=" + dbSize);

            //String randomKey()：从当前选定的数据库返回随机选择的 key
            System.out.println(jedis.randomKey());

            //删除当前数据库中所有key。此方法不会失败，慎用
            jedis.flushDB();
            //删除所有数据库中的所有key。此方法不会失败，更加慎用
            jedis.flushAll();

            //Long incr(final String key): 将key上存储的数字增加1,如果key不存在，则执行增量操作前自动新建并设置为0。
            Long wang = jedis.incr("wang");
            System.out.println(wang);
            System.out.println(jedis.incr("wang"));
            System.out.println(jedis.incr("wang"));


            jedis.set("code_1", "20009");
            int delKeyByScript = delKeyByScript("code_1", "20009");
            System.out.println("delKeyByScript=" + delKeyByScript);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            getErrMsg(resultMap, e);
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
     * 执行 lua 脚本——原子操作
     * Object eval(final String script)
     * Object eval(final String script, final List<String> keys, final List<String> args)
     * * keys 对应脚本中 KEYS[index] 占位符，index 是列表中的索引，从 1 开始，如 KEYS[1] 表示取其中第1个元素，
     * * args 对应脚本中 ARGV[index] 占位符，index 是列表中的索引，从 1 开始，如 ARGV[3] 表示取其中第3个元素，
     *
     * @param key
     * @param val
     * @return
     */
    public int delKeyByScript(String key, String val) {
        JedisConnection jedisConnection = null;
        try {
            jedisConnection = (JedisConnection) RedisConnectionUtils.getConnection(redisConnectionFactory, true);
            Jedis jedis = jedisConnection.getNativeConnection();
            if (jedis == null) {
                return 0;
            }
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object eval = jedis.eval(script, Lists.newArrayList(key), Lists.newArrayList(val));
            return Integer.valueOf(eval.toString());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (jedisConnection != null) {
                RedisConnectionUtils.releaseConnection(jedisConnection, redisConnectionFactory);
            }
        }
        return 0;
    }


    private void getErrMsg(Map<String, Object> resultMap, Exception e) {
        resultMap.put("code", 500);
        resultMap.put("msg", e.getMessage());
        resultMap.put("data", null);
    }
}
