package com.jason.liu.redis.support;

import com.jason.liu.redis.config.RedisKeySupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-05-26 16:07:32
 * @todo
 */
public abstract class AbstractRedisTemplateSerializerSupport<T extends RedisSerializer<Object>> extends RedisTemplate<String, Object> {

    private final RedisKeySupport supportProperty;

    private String applicationName;

    public AbstractRedisTemplateSerializerSupport(RedisConnectionFactory redisConnectionFactory,
                                                  RedisKeySupport redisKeySupport) {
        this.supportProperty = redisKeySupport;
        this.setConnectionFactory(redisConnectionFactory);
    }

    private void initSerializerSupport() {
        T valueSerializer = this.valueSerializerSupport();
        this.setKeySerializer(this.keySerializerSupport());
        this.setValueSerializer(valueSerializer);
        this.setHashKeySerializer(this.hashKeySerializerSupport());
        this.setHashValueSerializer(valueSerializer);
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

    /**
     * HashKey的序列化支持
     *
     * @return
     */
    public StringRedisSerializer hashKeySerializerSupport() {
        return new StringRedisSerializer();
    }

    /**
     * Value的序列化支持
     *
     * @return
     */
    public abstract T valueSerializerSupport();

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public void afterPropertiesSet() {
        this.initSerializerSupport();
        super.afterPropertiesSet();
    }
}
