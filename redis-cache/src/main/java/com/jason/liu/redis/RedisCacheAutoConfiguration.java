package com.jason.liu.redis;

import com.jason.liu.redis.config.MySqlRedisCacheProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-05-26 18:06:26
 * @todo
 */
@Configuration
@EnableConfigurationProperties(MySqlRedisCacheProperty.class)
public class RedisCacheAutoConfiguration {

    @Bean
    public CacheContext cacheContext(MySqlRedisCacheProperty mySqlRedisCacheProperty) {
        CacheContext cacheContext = CacheContext.instance();
        cacheContext.setMySqlRedisCacheProperty(mySqlRedisCacheProperty);
        return cacheContext;
    }


}
