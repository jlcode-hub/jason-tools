package com.jason.liu.redis;

import com.jason.liu.redis.config.RedisSupportProperty;
import com.jason.liu.redis.multi.LettuceRedisConnectionFactoryBean;
import com.jason.liu.redis.multi.MultiRedisFactoryBean;
import com.jason.liu.redis.support.RedisTemplateForString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Redis注册器
 *
 * @author meng.liu
 * @version 1.0
 * @date 2021-07-21 19:27:31
 */
@Slf4j
public abstract class AbstractRedisSupportRegistry implements BeanDefinitionRegistryPostProcessor, BeanPostProcessor {

    public static final String TEMPLATE_SUFFIX = "$$RedisTemplate";
    public static final String STRING_TEMPLATE_SUFFIX = "$$StringRedisTemplate";
    public static final String SUPPORT_SUFFIX = "$$Support";
    public static final String STRING_SUPPORT_ALIAS_PREFIX = "$";

    private BeanDefinitionRegistry registry;

    private ConfigurableListableBeanFactory beanFactory;

    private AtomicBoolean init = new AtomicBoolean(false);

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (init.compareAndSet(false, true)) {
            RedisSupportProperty redisSupportProperty = beanFactory.getBean(RedisSupportProperty.class);
            this.register(redisSupportProperty);
        }
        return bean;
    }

    /**
     * 注册
     *
     * @param redisSupportProperty
     */
    public abstract void register(RedisSupportProperty redisSupportProperty);


    protected void registerStringRedisTemplate(String name, String connName, RedisSupportProperty redisSupportProperty, boolean isPrimary) {
        String templateName = name + STRING_TEMPLATE_SUFFIX;
        BeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(RedisTemplateForString.class)
                .addConstructorArgReference(connName)
                .addConstructorArgValue(redisSupportProperty)
                .getBeanDefinition();
        beanDefinition.setPrimary(isPrimary);
        this.registeredBean(templateName, beanDefinition);

        BeanDefinition supportBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(StringRedisTemplateSupport.class)
                .addConstructorArgReference(templateName)
                .addConstructorArgValue(name)
                .addConstructorArgValue(isPrimary)
                .getBeanDefinition();
        supportBeanDefinition.setPrimary(isPrimary);
        String supportBeanName = templateName + SUPPORT_SUFFIX;
        this.registeredBean(supportBeanName, supportBeanDefinition);
        this.registerAlias(supportBeanName, STRING_SUPPORT_ALIAS_PREFIX + name);
    }

    protected String registerRedisConnectionFactory(String name, RedisSupportProperty redisSupportProperty, boolean isPrimary) {
        String connectionName = name + LettuceRedisConnectionFactoryBean.BEAN_NAME;
        BeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(LettuceRedisConnectionFactoryBean.class)
                .addConstructorArgValue(redisSupportProperty)
                .getBeanDefinition();
        beanDefinition.setPrimary(isPrimary);
        this.registeredBean(connectionName, beanDefinition);
        return connectionName;
    }

    protected void registerRedisTemplate(String name, String connName, RedisSupportProperty redisSupportProperty, boolean isPrimary) {
        String templateName = name + TEMPLATE_SUFFIX;
        BeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(MultiRedisFactoryBean.class)
                .addConstructorArgReference(connName)
                .addConstructorArgValue(redisSupportProperty)
                .getBeanDefinition();
        beanDefinition.setPrimary(isPrimary);
        this.registeredBean(templateName, beanDefinition);

        BeanDefinition supportBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(RedisTemplateSupport.class)
                .addConstructorArgReference(templateName)
                .addConstructorArgValue(name)
                .addConstructorArgValue(isPrimary)
                .getBeanDefinition();
        supportBeanDefinition.setPrimary(isPrimary);
        String supportBeanName = templateName + SUPPORT_SUFFIX;
        this.registeredBean(supportBeanName, supportBeanDefinition);
        this.registerAlias(supportBeanName, name);
    }

    protected void registeredBean(String beanName, BeanDefinition definition) {
        this.registry.registerBeanDefinition(beanName, definition);
        log.info("register bean that named {}", beanName);
    }

    protected void registerAlias(String beanName, String aliasName) {
        this.registry.registerAlias(beanName, aliasName);
        log.info("register alias name {} for the bean named {}", aliasName, beanName);
    }

}
