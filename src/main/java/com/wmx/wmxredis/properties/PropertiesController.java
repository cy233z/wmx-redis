package com.wmx.wmxredis.properties;

import com.wmx.wmxredis.annotation.RedisLock;
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

    @Resource
    private PersonProperties personProperties;

    @Resource
    private StaffProperties staffProperties;

    /**
     * ConfigurationProperties + Component  取值演示
     * <p>
     * http://localhost:8080/properties/getDefaultUser
     *
     * @return
     */
    @GetMapping("properties/getDefaultUser")
    @RedisLock(retryCount = 3, lockTime = 30, lockFiled = -1, desc = "redis分布式锁")
    public UserProperties getDefaultUser() {
        return userProperties;
    }

    /**
     * ConfigurationProperties + Bean 取值演示
     * <p>
     * http://localhost:8080/properties/getDefaultPerson
     *
     * @return
     */
    @GetMapping("properties/getDefaultPerson")
    public PersonProperties getDefaultPerson() {
        return personProperties;
    }

    /**
     * ConfigurationProperties + EnableConfigurationProperties 取值演示
     * <p>
     * http://localhost:8080/properties/getDefaultStaff
     *
     * @return
     */
    @GetMapping("properties/getDefaultStaff")
    public StaffProperties getDefaultStaff() {
        return staffProperties;
    }

}
