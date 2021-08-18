package com.jason.liu.redis.limiter;

import lombok.Data;

import java.io.Serializable;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-06-16 10:44:11
 * @todo
 */
@Data
public class RateLimiterConfig implements Serializable {
    /**
     * 令牌桶ID
     */
    private String id;
    /**
     * 最大令牌数
     */
    private Long maxPermits;
    /**
     * 时间间隔
     */
    private Long interval;

}
