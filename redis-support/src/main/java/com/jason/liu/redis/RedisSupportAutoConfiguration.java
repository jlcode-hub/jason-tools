package com.jason.liu.redis;

import com.jason.liu.redis.config.RedisSupportProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Redis支持自动装配入口，替代spring框架的RedisAutoConfiguration
 *
 * @author meng.liu
 * @version 1.0
 * @date 2021-07-21 09:36:03
 */
@Configuration
@EnableConfigurationProperties(RedisSupportProperty.class)
@Import(RedisSupportAutoRegistrar.class)
public class RedisSupportAutoConfiguration {

}
