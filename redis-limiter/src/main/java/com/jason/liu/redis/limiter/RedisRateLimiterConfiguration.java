package com.jason.liu.redis.limiter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-06-16 10:06:18
 * @todo
 */
@Configuration
public class RedisRateLimiterConfiguration {

    @Bean
    public RedisScript<Long> redisRateLimiterLuaScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis_rate_limiter.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisRateLimiterExecutor redisRateLimiter(
            RedisTemplate<String, Object> redisTemplate,
            @Qualifier(RedisRateLimiterExecutor.REDIS_RATE_LUA_SCRIPT_BEAN) RedisScript<Long> redisScript) {
        return new RedisRateLimiterExecutor(redisTemplate, redisScript);
    }

    @Bean
    public RedisRateLimiterFactory redisRateLimiterFactory(RedisRateLimiterExecutor redisRateLimiterExecutor) {
        return new RedisRateLimiterFactory(redisRateLimiterExecutor);
    }
}
