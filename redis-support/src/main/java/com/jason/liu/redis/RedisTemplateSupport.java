package com.jason.liu.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-05-26 15:47:07
 * @todo
 */
@Slf4j
public class RedisTemplateSupport extends BaseRedisTemplateSupport<Object> {

    public RedisTemplateSupport(RedisTemplate<String, Object> redisTemplate,
                                String id,
                                Boolean isPrimary) {
        super(redisTemplate, id, isPrimary);
    }
}
