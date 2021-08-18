package com.jason.liu.redis.multi;

import com.jason.liu.redis.config.RedisKeySupport;
import com.jason.liu.redis.config.RedisSupportProperty;
import com.jason.liu.redis.config.RedisTemplateSupportType;
import com.jason.liu.redis.support.AbstractRedisTemplateSerializerSupport;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-06-28 11:37:14
 * @todo
 */
public class MultiRedisFactoryBean implements FactoryBean<RedisTemplate<String, Object>>, InitializingBean {

    private RedisConnectionFactory connectionFactory;

    private RedisSupportProperty redisProperties;

    private AbstractRedisTemplateSerializerSupport<?> redisTemplate;

    @Value("${spring.application.name}")
    private String applicationName;

    public MultiRedisFactoryBean(RedisConnectionFactory connectionFactory,
                                 RedisSupportProperty redisProperties) {
        this.connectionFactory = connectionFactory;
        this.redisProperties = redisProperties;
        this.initRedisTemplate();
    }

    private void initRedisTemplate() {
        try {
            RedisTemplateSupportType supportType = redisProperties.getSupportType();
            redisTemplate = supportType.getSerializerSupport()
                    .getDeclaredConstructor(RedisConnectionFactory.class, RedisKeySupport.class)
                    .newInstance(connectionFactory, this.redisProperties);
        } catch (Exception e) {
            throw new RuntimeException("init redis template exception", e);
        }

    }

    @Override
    public RedisTemplate<String, Object> getObject() throws Exception {
        return redisTemplate;
    }

    @Override
    public Class<?> getObjectType() {
        return RedisTemplate.class;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        redisTemplate.setApplicationName(applicationName);
        redisTemplate.afterPropertiesSet();
    }
}
