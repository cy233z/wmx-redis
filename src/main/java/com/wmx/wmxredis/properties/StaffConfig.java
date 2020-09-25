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


}
