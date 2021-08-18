package com.jason.liu.redis.limiter;

import java.util.concurrent.TimeUnit;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-06-16 10:18:31
 * @todo
 */
public interface RedisRateLimiter {


    /**
     * 尝试获取1个令牌，如无法成功获取立即返回false，否则返回true
     *
     * @return
     */
    default boolean tryAcquire() {
        return tryAcquire(1);
    }

    /**
     * 尝试获取指定个数个令牌，如无法成功获取立即返回false，否则返回true
     *
     * @param permits
     * @return
     */
    boolean tryAcquire(long permits);

    /**
     * 一直等待直到获取到1个令牌
     * 不推荐使用！！！
     * 该方法可能导致程序出现死循环
     */
    default void acquire() {
        acquire(1);
    }

    /**
     * 一直等待直到获取指定个数的令牌
     * 不推荐使用！！！
     * 该方法可能导致程序出现死循环
     *
     * @param permits
     */
    default void acquire(long permits) {
        this.tryAcquire(permits, -1, null);
    }

    /**
     * 尝试获取1个令牌数，如无法获取则等待重试
     *
     * @param timeout
     * @param unit
     * @return
     */
    default boolean tryAcquire(long timeout, TimeUnit unit) {
        return this.tryAcquire(1, timeout, unit);
    }

    /**
     * 尝试获取指定令牌数，如无法获取则等待重试
     *
     * @param permits
     * @param timeout
     * @param unit
     * @return
     */
    boolean tryAcquire(long permits, long timeout, TimeUnit unit);


    /**
     * 当前可用令牌数
     *
     * @return
     */
    long availablePermits();

}
