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
import redis.clients.jedis.SortingParams;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Jedis 客户端 API 练习——操作 Redis 列表类型
 *
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
     * http://localhost:8080/jedis/setList?key=jedisList&value=100
     * http://localhost:8080/jedis/setList?key=jedisList&value=200&index=10
     * <p>
     * 将字符串值添加到列表的头部（LPUSH）或尾部（RPUSH），如果键不存在，将在追加操作之前创建一个空列表。如果key存在但不是列表，则返回错误。返回添加后列表中的元素个数。
     * Long rpush(final String key, final String... strings)
     * Long lpush(final String key, final String... strings)
     * 更新列表指定索引位置的元素。超出范围的索引将生成错误。与接受索引的其他列表命令类似，索引可以是负数，如-1是最后一个元素，-2是倒数第二个元素，依此类推。
     * String lset(final String key, final long index, final String value)
     *
     * @param key   ：键
     * @param value ：值
     * @return
     */
    @GetMapping("/jedis/setList")
    public Map<String, Object> setList(@RequestParam String key, @RequestParam String value, Integer index) {
        Map<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("code", 200);
        resultMap.put("msg", "success");

        JedisConnection jedisConnection = null;
        try {
            jedisConnection = (JedisConnection) RedisConnectionUtils.getConnection(redisConnectionFactory, true);

            // 有了 Jedis 则可以使用它的任意方法操作 Redis 了
            Jedis jedis = jedisConnection.getNativeConnection();

            if (index != null && index >= 0 && index < jedis.llen(key)) {
                //更新指定索引位置的元素
                jedis.lset(key, index, value);
            } else {
                //向列表末尾添加元素
                Long rpush = jedis.rpush(key, value);
                //向列表头部添加元素
                //Long lpush = jedis.lpush(key, value);
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
     * 获取 List 中的元素
     * http://localhost:8080/jedis/getList
     * <p>
     * 获取列表中指定索引范围内的元素，从0开始，-1表示倒数第一个元素，-2表示倒数第二个元素。超出范围的索引不会产生错误。
     * List<String> lrange(final String key, final long start, final long stop)
     * Long llen(final String key)：返回列表的长度，如果key不存在，则返回0（与空列表的行为相同）,如果键处存储的值不是列表，则返回错误。
     * List<String> sort(final String key)：对集合或列表进行排序(并不改变存储的值)。默认情况下，只能对数字进行排序，否则报错，元素作为双精度浮点数进行比较。这是最简单的排序形式
     * <p>
     * 获取指定索引位置的元素，索引从0开始，支持负索引，例如-1是最后一个元素，-2是倒数第二个元素，依此类推。
     * 如果key对应的值不是列表类型，则返回错误。如果索引超出范围，则返回 null
     * String lindex(final String key, final long index)
     *
     * @return
     */
    @GetMapping("/jedis/getList")
    public Map<String, Object> getList() {
        Map<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("code", 200);
        resultMap.put("msg", "success");

        JedisConnection jedisConnection = null;
        try {
            jedisConnection = (JedisConnection) RedisConnectionUtils.getConnection(redisConnectionFactory, true);

            // 有了 Jedis 则可以使用它的任意方法操作 Redis 了
            Jedis jedis = jedisConnection.getNativeConnection();

            jedis.del("mylist1");
            jedis.lpush("mylist1", "1");
            jedis.lpush("mylist1", "40");
            jedis.lpush("mylist1", "6");
            jedis.lpush("mylist1", "45");
            jedis.lpush("mylist1", "10");
            jedis.lpush("mylist1", "56");

            //获取列表的长度
            Long llen = jedis.llen("mylist1");
            System.out.println("列表长度：" + llen);

            //对返回的元素进行排序，如果元素中存在非数字，则报错：JedisDataException: ERR One or more scores can't be converted into double
            List<String> sort = jedis.sort("mylist1");
            System.out.println("sort=" + sort);

            //获取指定范围内的元素
            List<String> lrange = jedis.lrange("mylist1", 0, -1);
            System.out.println("lrange=" + lrange);

            jedis.del("mylist");
            jedis.lpush("mylist", "1");
            jedis.lpush("mylist", "40");
            jedis.lpush("mylist", "6");
            jedis.lpush("mylist", "a");
            jedis.lpush("mylist", "FK");
            jedis.lpush("mylist", "AH");
            jedis.lpush("mylist", "中国");

            //使用排序参数 SortingParams 对数字以外的值进行排序,会改变实际存储的值的顺序
            //SortingParams desc()：降序排序
            //SortingParams asc()：升序排序(默认)
            //SortingParams alpha()：按字典顺序排序(支持任意字符)，默认只能对数字排序
            //SortingParams limit(final int start, final int count)：分页查询(限制返回的条数), start 开始位置的索引，从零开始；count 获取的数量
            SortingParams sortingParameters = new SortingParams();
            sortingParameters.desc();
            sortingParameters.alpha();
            sortingParameters.limit(0, jedis.llen("mylist").intValue());
            List<String> stringList = jedis.sort("mylist", sortingParameters);
            //[中国, a, FK, AH, 6, 40, 1]
            System.out.println("sortingParameters=" + stringList);

            System.out.println(jedis.lrange("mylist", 0, -1));

            String lindex = jedis.lindex("mylist", 1);
            System.out.println("lindex=" + lindex);

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

    /**
     * http://localhost:8080/jedis/delList?key=jedisList
     * http://localhost:8080/jedis/delList?key=jedisList&index=2&value=200
     * http://localhost:8080/jedis/delList?key=jedisList&start=0&stop=5
     * <p>
     * 从列表中删除指定计数(count)的元素，如果计数为零，则删除所有的 value。如果计数为负值，则从后向前寻找元素并删除。
     * 返回成功删除的个数，不存在key将始终返回0。
     * Long lrem(final String key, final long count, final String value)
     * <p>
     * 修剪现有列表，使其仅包含指定范围[start,stop]的元素。索引从0开始，支持负数，-1是列表的最后一个元素。
     * 超出范围的索引不会产生错误：如果 start > stop，将留下一个空列表作为值。
     * String ltrim(final String key, final long start, final long stop)
     * <p>
     * 以原子方式返回并删除列表的第一个(LPOP)或最后一个(RPOP)元素，如果key不存在或列表已为空，则返回 null
     * String lpop(final String key)
     *
     * @param key
     * @param count
     * @param value
     * @return
     */
    @GetMapping("/jedis/delList")
    public Map<String, Object> delList(@RequestParam String key, Integer count, String value, Integer start, Integer stop) {
        Map<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("code", 200);
        resultMap.put("msg", "success");

        JedisConnection jedisConnection = null;
        try {
            jedisConnection = (JedisConnection) RedisConnectionUtils.getConnection(redisConnectionFactory, true);
            Jedis jedis = jedisConnection.getNativeConnection();

            if (count != null && value != null) {
                //删除列表中指定的元素 value，count 是累计删除的个数
                Long lrem = jedis.lrem(key, count.longValue(), value);
                System.out.println("lrem=" + lrem);
            }

            if (start != null && stop != null) {
                //删除访问以外的元素
                String ltrim = jedis.ltrim(key, start, stop);
                System.out.println("ltrim=" + ltrim);
            }

            //删除末尾的元素并返回
            String rpop = jedis.rpop(key);
            resultMap.put("data", rpop);
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
