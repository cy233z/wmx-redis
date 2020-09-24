package com.wmx.wmxredis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author wangMaoXiong
 */
@SpringBootApplication
@ServletComponentScan
public class WmxRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(WmxRedisApplication.class, args);
    }

}
