package com.wmx.wmxredis.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 自定义 Redisson  配置选项配置类
 * <p>
 * 1、EnableConfigurationProperties：将 {@link RedssionProperties} 配置类交由 Spring 容器管理
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2020/9/24 19:28
 */
@Configuration
@EnableConfigurationProperties(RedssionProperties.class)
public class RedissonConfig {

    private final RedssionProperties redssionProperties;

    /**
     * 自定义 Redisson  配置选项
     * 通过构造器从 Spring 容器中获取 {@link RedssionProperties}实例
     *
     * @param redssionProperties
     */
    public RedissonConfig(RedssionProperties redssionProperties) {
        this.redssionProperties = redssionProperties;
    }


    /**
     * 自定义 Redisson  配置选项
     * redis 服务器单机部署时，创建 RedissonClient 实例，交由 Spring 容器管理
     * 只有当配置了 redisson.type=stand-alone 时，才继续生成 RedissonClient 实例并交由 Spring 容器管理
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "redisson", name = "type", havingValue = "stand-alone")
    public RedissonClient redissonClient() {
        /**
         * Config：Redisson 配置基类，SingleServerConfig：单机部署配置类，MasterSlaveServersConfig：主从复制部署配置
         * SentinelServersConfig：哨兵模式配置，ClusterServersConfig：集群部署配置类。
         * useSingleServer()：初始化 redis 单服务器配置。即 redis 服务器单机部署
         * setAddress(String address)：设置 redis 服务器地址。格式 -- redis://主机:端口，不写时，默认为 redis://127.0.0.1:6379
         * setDatabase(int database): 设置连接的 redis 数据库，默认为 0
         * setPassword(String password)：设置 redis 服务器认证密码，没有时设置为 null，默认为 null
         * RedissonClient create(Config config): 使用提供的配置创建同步/异步 Redisson 实例
         * Redisson 类实现了 RedissonClient 接口，真正需要使用的就是这两个 API
         */
        Config config = new Config();
        config.useSingleServer()
                .setAddress(redssionProperties.getAddress())
                .setDatabase(redssionProperties.getDatabase())
                .setPassword(redssionProperties.getPassword())
                .setConnectionPoolSize(redssionProperties.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(redssionProperties.getConnectionMinimumIdleSize())
                .setTimeout(redssionProperties.getTimeout())
                .setConnectTimeout(redssionProperties.getConnectTimeout());
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
