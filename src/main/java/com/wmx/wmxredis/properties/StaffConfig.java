package com.wmx.wmxredis.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 * EnableConfigurationProperties：将目标 java bean 配置类(ConfigurationProperties) 交由 Spring 容器管理
 * 而后可以在任意地方使用 取值使用.
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2020/9/25 11:02
 */
@Configuration
@EnableConfigurationProperties(StaffProperties.class)
public class StaffConfig {
    /**
     * 可以参考官方的 KafkaAutoConfiguration 就知道，如果想在配置类中直接获取目标 java bean 属性配置类，
     * 则直接通过构造器传入即可，通常用于为创建其他 bean 时提供属性配置。这里不再继续深入，实际中参考官网一目了然。
     */
    private final StaffProperties properties;

    /**
     * 应用启动的时候会自动执行本来，完成赋值。
     *
     * @param properties
     */
    public StaffConfig(StaffProperties properties) {
        this.properties = properties;
        System.out.println("properties=" + this.properties);
    }


}
