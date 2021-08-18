package com.jason.liu.redis.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-05-31 14:19:23
 * @todo
 */
@Slf4j
public class PrefixStringRedisSerializer extends StringRedisSerializer {

    private static final String DEFAULT_PREFIX = "[JasonToolsUnnamedServer]";

    private static final String JOINER = ":";

    private String prefix;

    private RedisTemplate<String, ?> redisTemplate;

    public PrefixStringRedisSerializer(String prefix, RedisTemplate<String, ?> redisTemplate) {
        if (StringUtils.isBlank(prefix)) {
            log.warn("the key prefix for redis is not configured, use the default {}", DEFAULT_PREFIX);
            this.prefix = DEFAULT_PREFIX;
        } else {
            this.prefix = prefix;
        }
        log.info("the key prefix for redis is {}", prefix);
        if (!this.prefix.endsWith(JOINER)) {
            this.prefix = this.prefix + JOINER;
        }
        this.redisTemplate = redisTemplate;
    }

    @Override
    public byte[] serialize(String string) {
        if (KeyPrefixHolder.isWithPrefix(this.redisTemplate) && !string.startsWith(this.prefix)) {
            return super.serialize(this.prefix + string);
        } else {
            return super.serialize(string);
        }
    }
}
