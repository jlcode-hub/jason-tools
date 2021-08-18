package com.jason.liu.redis.config;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-06-28 11:57:19
 * @todo
 */
public interface RedisKeySupport {

    /**
     * Redis Key的前缀
     */
    String getKeyPrefix();

    /**
     * 是否开启Key前缀的配置，默认开启
     */
    Boolean getUseKeyPrefix();

}
