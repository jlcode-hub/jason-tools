package com.jason.liu.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-05-26 15:47:07
 * @todo
 */
@Slf4j
public class StringRedisTemplateSupport extends BaseRedisTemplateSupport<String> {

    private final ObjectMapper om;

    public StringRedisTemplateSupport(RedisTemplate<String, String> redisTemplate,
                                      String id,
                                      Boolean isPrimary) {
        super(redisTemplate, id, isPrimary);
        this.om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    }

    @Override
    public <T> T getValue(String key, Class<T> tClass) {
        String object = this.getValue(key);
        if (null == object) {
            return null;
        }
        try {
            return this.om.readValue(object, tClass);
        } catch (Exception e) {
            log.warn("parse object for key {} to class {} exception.", key, tClass);
            return null;
        }
    }
}
