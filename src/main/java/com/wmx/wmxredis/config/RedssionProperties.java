package com.wmx.wmxredis.config;

import org.redisson.config.BaseConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wangMaoXiong
 * 自定义 Redssion 配置属性，这些属性可以参考 {@link SingleServerConfig}、{@link BaseConfig}、{@link Config}
 * Redssion 官网配置方法：https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95
 */
@ConfigurationProperties(prefix = "redisson")
public class RedssionProperties {
    /**
     * Redis 服务器地址
     */
    private String address;
    /**
     * 用于Redis连接的数据库索引
     */
    private int database = 0;
    /**
     * Redis身份验证的密码，如果不需要，则应为null
     */
    private String password;
    /**
     * Redis最小空闲连接量
     */
    private int connectionMinimumIdleSize = 24;
    /**
     * Redis连接最大池大小
     */
    private int connectionPoolSize = 64;
    /**
     * Redis 服务器响应超时时间，Redis 命令成功发送后开始倒计时（毫秒）
     */
    private int timeout = 3000;
    /**
     * 连接到 Redis 服务器时超时时间（毫秒）
     */
    private int connectTimeout = 10000;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getConnectionMinimumIdleSize() {
        return connectionMinimumIdleSize;
    }

    public void setConnectionMinimumIdleSize(int connectionMinimumIdleSize) {
        this.connectionMinimumIdleSize = connectionMinimumIdleSize;
    }

    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
}
