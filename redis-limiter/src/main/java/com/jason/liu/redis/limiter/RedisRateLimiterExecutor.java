package com.jason.liu.redis.limiter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-06-16 09:59:00
 * @todo
 */
@Slf4j
public class RedisRateLimiterExecutor {

    private static final String AVAILABLE_PERMITS = ":availablePermits";

    private static final String PERMITS = ":permits";

    public static final String REDIS_RATE_LUA_SCRIPT_BEAN = "redisRateLimiterLuaScript";

    private final AtomicBoolean init = new AtomicBoolean(false);

    private RedisTemplate<String, Object> redisTemplate;

    private RedisScript<Long> script;

    public RedisRateLimiterExecutor(RedisTemplate<String, Object> redisTemplate,
                                    RedisScript<Long> script) {
        this.redisTemplate = redisTemplate;
        this.script = script;
    }

    private List<String> rateLimiterKeys(String id) {
        return Arrays.asList(
                id + AVAILABLE_PERMITS,
                id + PERMITS
        );
    }

    /**
     * 调用Redis脚本获取令牌
     *
     * @param id
     * @param permits
     * @param maxPermits
     * @param interval
     * @return
     */
    final boolean tryAcquire(String id, Long permits, Long timeout, Long maxPermits, Long interval) {
        long startTime = System.currentTimeMillis();
        Long waitTime = tryAcquire(id, permits, maxPermits, interval);
        //todo 成功获取令牌
        if (null == waitTime) {
            return true;
        }
        if (timeout == -1) {
            try {
                log.debug("acquire permit failed, try again after {}ms", waitTime);
                TimeUnit.MILLISECONDS.sleep(waitTime);
                return tryAcquire(id, permits, -1L, maxPermits, interval);
            } catch (Exception e) {
                log.debug("try acquire exception", e);
                return false;
            }
        }
        long currentTime = System.currentTimeMillis();
        //todo redis执行耗时
        long executorTime = currentTime - startTime;
        //todo 减去执行耗时之后的剩余等待时长
        long remainTime = timeout - executorTime;
        if (remainTime <= 0) {
            //todo redis执行时长超过到了超时等待时长而导致了超时
            return false;
        }
        //todo 最近的失效时长大于最近失效时长
        if (waitTime > timeout) {
            return false;
        }
        try {
            log.debug("acquire permit failed, try again after {}ms", waitTime);
            TimeUnit.MILLISECONDS.sleep(waitTime);
            long delayTime = System.currentTimeMillis() - currentTime;
            if (delayTime >= remainTime) {
                return false;
            }
            return tryAcquire(id, permits, remainTime - delayTime, maxPermits, interval);
        } catch (Exception e) {
            log.debug("try acquire exception", e);
            return false;
        }
    }

    /**
     * 调用Redis脚本获取令牌
     *
     * @param id
     * @param permits
     * @param maxPermits
     * @param interval   单位：毫秒
     * @return
     */
    public final Long tryAcquire(String id, Long permits, Long maxPermits, Long interval) {
        if (permits > maxPermits) {
            throw new IllegalArgumentException("request permits amount could not exceed the max permits.");
        }
        if (interval < 1) {
            throw new IllegalArgumentException("the interval must be great than 1.");
        }
        try {
            return this.redisTemplate.execute(this.script, this.rateLimiterKeys(id), System.currentTimeMillis(), permits, maxPermits, TimeUnit.SECONDS.toMillis(interval), ThreadLocalRandom.current().nextLong());
        } catch (Exception e) {
            log.warn("execute limit lua script exception, cause: {}", e.getMessage());
            if (log.isDebugEnabled()) {
                log.warn("", e);
            }
            //todo 脚本执行失败，为了保护下游服务，默认让程序100m后重试
            return 100L;
        }
    }

    public final Long getAvailablePermits(String id) {
        Long availablePermits = (Long) this.redisTemplate.opsForValue().get(id + AVAILABLE_PERMITS);
        if (null == availablePermits) {
            return 0L;
        } else {
            return availablePermits;
        }
    }
}
