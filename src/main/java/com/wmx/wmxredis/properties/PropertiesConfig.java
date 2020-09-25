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

    @ConfigurationProperties(prefix = "person")
    @Bean
    public PersonProperties personProperties() {
        return new PersonProperties();
    }

}
