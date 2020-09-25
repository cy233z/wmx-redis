package com.wmx.wmxredis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置类
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2020/9/24 19:28
 */
@Configuration
public class RedissonConfig {

    @Autowired
    private RedisProperties properties;

    /**
     * redis 服务器单机部署时，创建 RedissonClient 实例。
     *
     * @return
     */
    @Bean
    public RedissonClient redissonClient() {

        System.out.println(properties);
        System.out.println(properties.getDatabase());

        /**
         * Config：Redisson 配置类
         * SingleServerConfig useSingleServer()：初始化 redis 单服务器配置。即 redis 服务器单机部署
         * setAddress(String address)：设置 redis 服务器地址。格式 -- 主机:端口
         * RedissonClient create(Config config): 使用提供的配置创建同步/异步 Redisson 实例
         * RedissonClient create()：默认连接本地 127.0.0.1:6379
         * Redisson 类实现了 RedissonClient 接口
         */
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

}
