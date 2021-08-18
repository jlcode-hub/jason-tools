package com.jason.liu.redis.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-05-26 16:19:55
 * @todo
 */
@Data
@ConfigurationProperties("spring.redis")
public class RedisSupportProperty extends RedisProperties implements RedisKeySupport, Serializable {
    /**
     * 是否使用Key前缀，默认为true
     */
    private Boolean useKeyPrefix = true;

    /**
     * Redis Key的前缀
     */
    private String keyPrefix;

    /**
     * 序列化支持，默认使用Jackson2
     */
    private RedisTemplateSupportType supportType = RedisTemplateSupportType.Jackson2;

    /**
     * 默认Bean的名称
     */
    private String primary;

    /**
     * 多数据源配置
     */
    private Map<String, RedisSupportProperty> multi = new HashMap<>();
}
