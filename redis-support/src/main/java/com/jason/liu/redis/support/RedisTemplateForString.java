package com.jason.liu.redis.support;

import com.jason.liu.redis.config.RedisKeySupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-05-31 14:28:13
 * @todo
 */
public class RedisTemplateForString extends StringRedisTemplate {

    private final RedisKeySupport supportProperty;

    @Value("${spring.application.name:}")
    private String applicationName;

    public RedisTemplateForString(RedisConnectionFactory redisConnectionFactory,
                                  RedisKeySupport redisKeySupport) {
        this.supportProperty = redisKeySupport;
        this.setConnectionFactory(redisConnectionFactory);
    }

    protected void initSerializerSupport() {
        this.setKeySerializer(this.keySerializerSupport());
    }

    /**
     * Key的序列化支持
     *
     * @return
     */
    public StringRedisSerializer keySerializerSupport() {
        if (supportProperty.getUseKeyPrefix()) {
            String prefixKey = StringUtils.isNotBlank(supportProperty.getKeyPrefix()) ? supportProperty.getKeyPrefix() : applicationName;
            return new PrefixStringRedisSerializer(prefixKey, this);
        } else {
            return StringRedisSerializer.UTF_8;
        }
    }

    @Override
    public void afterPropertiesSet() {
        this.initSerializerSupport();
        super.afterPropertiesSet();
    }
}
