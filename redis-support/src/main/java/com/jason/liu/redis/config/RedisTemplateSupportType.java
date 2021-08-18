package com.jason.liu.redis.config;

import com.jason.liu.redis.support.AbstractRedisTemplateSerializerSupport;
import com.jason.liu.redis.support.RedisTemplateForFastjson;
import com.jason.liu.redis.support.RedisTemplateForJackson2;
import lombok.Getter;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.Serializable;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-05-26 17:15:28
 * @todo RedisTemplate的类型
 */
@Getter
public enum RedisTemplateSupportType implements Serializable {
    /**
     * Jason2
     */
    Jackson2(RedisTemplateForJackson2.class),
    /**
     * Fastjson
     */
    Fastjson(RedisTemplateForFastjson.class);

    private Class<? extends AbstractRedisTemplateSerializerSupport<? extends RedisSerializer<Object>>> serializerSupport;

    RedisTemplateSupportType(Class<? extends AbstractRedisTemplateSerializerSupport<? extends RedisSerializer<Object>>> serializerSupport) {
        this.serializerSupport = serializerSupport;
    }
}
