package com.jason.liu.redis.limiter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-06-16 15:34:02
 * @todo
 */
public class RedisRateLimiterFactory implements InitializingBean {

    private static RedisRateLimiterFactory Instance;

    private RedisRateLimiterExecutor redisRateLimiterExecutor;

    RedisRateLimiterFactory(RedisRateLimiterExecutor redisRateLimiterExecutor) {
        this.redisRateLimiterExecutor = redisRateLimiterExecutor;
    }

    /**
     * 创建一个
     *
     * @param id
     * @param permits
     * @param interval
     * @return
     */
    public static RedisRateLimiter create(String id, Long permits, Long interval) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("redis rate limit id cannot be blank.");
        }
        if (permits < 1 || interval < 1) {
            throw new IllegalArgumentException("the permits or the interval for the redis rate cannot less than 1.");
        }
        RateLimiterConfig config = new RateLimiterConfig();
        config.setId(id);
        config.setMaxPermits(permits);
        config.setInterval(interval);
        return new RedisRateLimiterSupport(Instance.redisRateLimiterExecutor, config);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RedisRateLimiterFactory.Instance = this;
    }
}
