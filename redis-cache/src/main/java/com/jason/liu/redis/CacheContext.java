package com.jason.liu.redis;

import com.jason.liu.redis.config.MySqlRedisCacheProperty;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-05-26 17:57:10
 * @todo
 */
public class CacheContext implements ApplicationContextAware {

    private static CacheContext instance;

    private ApplicationContext applicationContext;

    @Setter
    private MySqlRedisCacheProperty mySqlRedisCacheProperty;

    protected static CacheContext instance() {
        instance = new CacheContext();
        return instance;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static ApplicationContext getSpringApplicationContext() {
        return instance.applicationContext;
    }

    public static RedisTemplateSupport getRedisTemplate() {
        return getSpringApplicationContext().getBean(RedisTemplateSupport.class);
    }

    public static MySqlRedisCacheProperty getMysqlRedisCacheProperty() {
        return instance.mySqlRedisCacheProperty;
    }

}
