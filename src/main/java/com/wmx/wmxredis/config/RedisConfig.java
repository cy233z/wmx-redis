package com.wmx.wmxredis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置
 *
 * @author wangMaoXiong
 */
@Configuration
@SuppressWarnings("Duplicated")
public class RedisConfig {

    /**
     * 自定义 RedisTemplate 序列化方式
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        //创建 RedisTemplate，key 和 value 都采用了 Object 类型
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        //绑定 RedisConnectionFactory
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //创建 Jackson2JsonRedisSerializer 序列方式，对象类型使用 Object 类型，
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        //设置一下 jackJson 的 ObjectMapper 对象参数
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 设置 RedisTemplate 序列化规则。因为 key 通常是普通的字符串，所以使用 StringRedisSerializer 即可。
        // 而 value 是对象时，才需要使用序列化与反序列化
        // key 序列化规则
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // value 序列化规则
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // hash key 序列化规则
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // hash value 序列化规则
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        //属性设置后操作
        redisTemplate.afterPropertiesSet();
        //返回设置好的 RedisTemplate
        return redisTemplate;
    }

    /**
     * 自定义 RedisTemplate 序列化方式 - 支持 Redis 事务
     * 在需要使用 redis 事务的地方直接注入名称为 redisTemplateTransactional 的 RedisTemplate 实例即可使用：multi(开启事务) 、exec（执行事务） 、discard（丢弃事务
     * 而不再需要手动 redisTemplate.setEnableTransactionSupport(true) 操作。
     * <pre>
     *     @Resource
     *     @Qualifier("redisTemplateTransactional")
     *     private RedisTemplate redisTemplateTransactional;
     *
     *     redisTemplate.multi();
     *     ......
     *     redisTemplate.exec();
     * </pre>
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplateTransactional(RedisConnectionFactory redisConnectionFactory) {
        //创建 RedisTemplate，key 和 value 都采用了 Object 类型
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        // 设置是否启用事务支持（默认是false）
        redisTemplate.setEnableTransactionSupport(true);
        //绑定 RedisConnectionFactory
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //创建 Jackson2JsonRedisSerializer 序列方式，对象类型使用 Object 类型，
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        //设置一下 jackJson 的 ObjectMapper 对象参数
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 设置 RedisTemplate 序列化规则。因为 key 通常是普通的字符串，所以使用 StringRedisSerializer 即可。
        // 而 value 是对象时，才需要使用序列化与反序列化
        // key 序列化规则
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // value 序列化规则
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // hash key 序列化规则
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // hash value 序列化规则
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        //属性设置后操作
        redisTemplate.afterPropertiesSet();
        //返回设置好的 RedisTemplate
        return redisTemplate;
    }

}
