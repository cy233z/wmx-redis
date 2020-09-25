package com.wmx.wmxredis.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2020/9/25 10:11
 */
@Configuration
public class PropertiesConfig {

    /**
     * Bean ：将对象实例交由 Spring 容器管理
     * ConfigurationProperties ：为实例注入配置
     * 组合起来的效果就是：这个交由 Spring 容器管理的 PersonProperties 实例，自身被注入了全局配置文件中的配置值，
     * 然后可以在其他地方获取 PersonProperties 并使用
     *
     * @return
     */
    @ConfigurationProperties(prefix = "person")
    @Bean
    public PersonProperties personProperties() {
        return new PersonProperties();
    }

}
