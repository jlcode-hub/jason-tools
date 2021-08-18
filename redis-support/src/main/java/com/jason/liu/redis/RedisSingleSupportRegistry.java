package com.jason.liu.redis;

import com.jason.liu.redis.config.RedisSupportProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * 单Redis装配
 *
 * @author meng.liu
 * @version v1.0
 * @date 2021-05-26 16:14:47
 * @todo
 */
@Slf4j
public class RedisSingleSupportRegistry extends AbstractRedisSupportRegistry {

    @Override
    public void register(RedisSupportProperty redisSupportProperty) {
        String connName = this.registerRedisConnectionFactory("master", redisSupportProperty, true);
        this.registerRedisTemplate("master", connName, redisSupportProperty, true);
        this.registerStringRedisTemplate("master", connName, redisSupportProperty, true);
    }

}

