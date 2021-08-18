package com.jason.liu.redis.multi;

import com.jason.liu.redis.config.RedisSupportProperty;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-06-29 09:25:16
 * @todo
 */
public class LettuceRedisConnectionFactoryBean implements FactoryBean<RedisConnectionFactory>, InitializingBean {

    public static final String BEAN_NAME = "$$LettuceRedisConnectionFactory";

    private RedisSupportProperty redisSupportProperty;

    private LettuceConnectionFactory redisConnectionFactory;

    public LettuceRedisConnectionFactoryBean(RedisSupportProperty redisSupportProperty) {
        this.redisSupportProperty = redisSupportProperty;
        this.initRedisConnectionFactory();
    }


    @Override
    public RedisConnectionFactory getObject() throws Exception {
        return this.redisConnectionFactory;
    }


    private void initRedisConnectionFactory() {
        LettuceConnectionFactoryBuilder lettuceConnectionFactoryBuilder = new LettuceConnectionFactoryBuilder(this.redisSupportProperty);
        this.redisConnectionFactory = lettuceConnectionFactoryBuilder.build();
    }

    @Override
    public Class<?> getObjectType() {
        return RedisConnectionFactory.class;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.redisConnectionFactory.afterPropertiesSet();
    }
}
