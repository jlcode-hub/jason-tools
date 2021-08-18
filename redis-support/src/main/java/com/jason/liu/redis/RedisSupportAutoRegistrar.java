package com.jason.liu.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Redis Bean装配注册器，通过配置选择装配单数据源或多数据源
 *
 * @author meng.liu
 * @version v1.0
 * @date 2021-06-28 10:30:05
 * @todo
 */
@Slf4j
public class RedisSupportAutoRegistrar implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private static final String CONFIG = "spring.redis.enable-multi-source";

    private AtomicBoolean init = new AtomicBoolean(false);

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (init.compareAndSet(false, true)) {
            BeanDefinition supportFactory = BeanDefinitionBuilder.rootBeanDefinition(RedisTemplateSupportFactory.class)
                    .getBeanDefinition();
            registry.registerBeanDefinition(RedisTemplateSupportFactory.BEAN_NAME, supportFactory);
            if (environment.getProperty(CONFIG, Boolean.class, false)) {
                AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(RedisMultiSourceSupportRegistry.class)
                        .getBeanDefinition();
                registry.registerBeanDefinition(RedisMultiSourceSupportRegistry.class.getSimpleName(), beanDefinition);
                log.info("[RedisSupport]: register redis support for multi source redis.");
            } else {
                AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(RedisSingleSupportRegistry.class)
                        .getBeanDefinition();
                registry.registerBeanDefinition(RedisSingleSupportRegistry.class.getSimpleName(), beanDefinition);
                log.info("[RedisSupport]: register redis support for default redis.");
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
