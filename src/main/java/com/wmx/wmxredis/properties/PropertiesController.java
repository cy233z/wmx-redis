package com.wmx.wmxredis.properties;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * ConfigurationProperties 为 java bean 注入属性值
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2020/9/25 9:18
 */
@RestController
public class PropertiesController {

    /**
     * 从容器中获取实例
     */
    @Resource
    private UserProperties userProperties;

    /**
     * http://localhost:8080/redisson/properties/getDefaultUser
     *
     * @return
     */
    @GetMapping("properties/getDefaultUser")
    public UserProperties getDefaultUser() {
        return userProperties;
    }

}
