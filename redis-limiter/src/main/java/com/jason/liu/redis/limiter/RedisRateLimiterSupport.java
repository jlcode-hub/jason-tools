package com.jason.liu.redis.limiter;

import java.util.concurrent.TimeUnit;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-06-16 10:43:29
 * @todo
 */
public class RedisRateLimiterSupport implements RedisRateLimiter {

    private RedisRateLimiterExecutor redisRateLimiterExecutor;

    private RateLimiterConfig config;

    RedisRateLimiterSupport(RedisRateLimiterExecutor redisRateLimiterExecutor, RateLimiterConfig config) {
        this.redisRateLimiterExecutor = redisRateLimiterExecutor;
        this.config = config;
    }

    @Override
    public boolean tryAcquire() {
        return this.tryAcquire(1);
    }

    @Override
    public boolean tryAcquire(long permits) {
        return this.redisRateLimiterExecutor.tryAcquire(this.config.getId(),
                permits, this.config.getMaxPermits(), this.config.getInterval()) == null;
    }


    @Override
    public boolean tryAcquire(long permits, long timeout, TimeUnit unit) {
        return this.redisRateLimiterExecutor.tryAcquire(this.config.getId(),
                permits, unit.toMillis(timeout), this.config.getMaxPermits(), this.config.getInterval());
    }

    @Override
    public long availablePermits() {
        return this.redisRateLimiterExecutor.getAvailablePermits(this.config.getId());
    }
}
