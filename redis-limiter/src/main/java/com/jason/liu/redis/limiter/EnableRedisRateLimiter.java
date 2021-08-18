package com.jason.liu.redis.limiter;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用Redis限流器
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RedisRateLimiterConfiguration.class})
public @interface EnableRedisRateLimiter {
}
