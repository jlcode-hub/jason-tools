package com.jason.liu.redis;

import com.jason.liu.redis.config.RedisSupportProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 多数据源Redis装配
 *
 * @author meng.liu
 * @version v1.0
 * @date 2021-06-28 09:48:46
 * @todo
 */
@Slf4j
public class RedisMultiSourceSupportRegistry extends AbstractRedisSupportRegistry {

    @Override
    public void register(RedisSupportProperty redisSupportProperty) {
        String primary = redisSupportProperty.getPrimary();
        if (StringUtils.isBlank(primary)) {
            throw new IllegalArgumentException("[spring.redis.primary] must be configured.");
        }
        Map<String, RedisSupportProperty> multiSourceProperty = redisSupportProperty.getMulti();
        if (!multiSourceProperty.containsKey(primary)) {
            throw new IllegalArgumentException("the value of [spring.redis.primary] is invalid.");
        }
        redisSupportProperty.getMulti().forEach((redisName, rsp) -> {
            boolean isPrimary = primary.equals(redisName);
            String connName = this.registerRedisConnectionFactory(redisName, rsp, isPrimary);
            this.registerRedisTemplate(redisName, connName, rsp, isPrimary);
            this.registerStringRedisTemplate(redisName, connName, rsp, isPrimary);
        });
    }
}
