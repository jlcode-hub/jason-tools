package com.jason.liu.redis.support;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.jason.liu.redis.config.RedisKeySupport;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-05-26 15:59:26
 * @todo
 */
public class RedisTemplateForFastjson extends AbstractRedisTemplateSerializerSupport<FastJsonRedisSerializer<Object>> {

    public RedisTemplateForFastjson(RedisConnectionFactory redisConnectionFactory,
                                    RedisKeySupport redisKeySupport) {
        super(redisConnectionFactory, redisKeySupport);
    }

    @Override
    public FastJsonRedisSerializer<Object> valueSerializerSupport() {
        return new FastJsonRedisSerializer<>(Object.class);
    }

}

