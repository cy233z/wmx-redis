package com.wmx.wmxredis.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URL;

/**
 * Redisson  yml 文件方式配置
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2020/9/24 19:28
 */
@Configuration
public class RedissonConfig2 {

    private static final Logger log = LoggerFactory.getLogger(RedissonConfig2.class);

    /**
     * 从 yaml 文件读取 redisson 配置对象
     * Config fromYAML(File file)
     * Config fromYAML(File file, ClassLoader classLoader)
     * Config fromYAML(InputStream inputStream)
     * Config fromYAML(Reader reader)
     * Config fromYAML(String content)
     * Config fromYAML(URL url)
     * <p>
     * String toYAML() ：将当前配置转换为YAML格式
     *
     * @return
     */
    @Bean
    public RedissonClient redissonClient() throws IOException {
        URL resource = RedissonConfig2.class.getClassLoader().getResource("redisson-config.yml");
        Config config = Config.fromYAML(resource);
        RedissonClient redissonClient = Redisson.create(config);
        log.info("Redisson 配置:{}", config.toYAML());
        return redissonClient;
    }
}
