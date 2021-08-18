package com.jason.liu.nacos.refresh;

import com.alibaba.nacos.spring.core.env.NacosPropertySource;
import com.jason.liu.nacos.refresh.utils.NacosLoggingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: meng.liu
 * @date: 2021/2/3
 * TODO: 监听器，在上下文环境变量准备好了之后初始化Nacos配置的日志
 */
public class NacosLoggingPreparedListener implements ApplicationListener<ApplicationPreparedEvent>, Ordered {

    private static final Logger log = LoggerFactory.getLogger(NacosLoggingPreparedListener.class);

    /**
     * 日志配置依赖于{@link LoggingSystem}，LoggingSystem在{@link LoggingApplicationListener}中被声明成bean
     */
    public static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 21;

    public static final String ROOT_LOGGER_NAME = "ROOT";

    private int order = DEFAULT_ORDER;

    private NacosLoggingContext nacosLoggingContext;

    private static AtomicBoolean isFirstLoad = new AtomicBoolean(true);

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        if (!isFirstLoad.get()) {
            return;
        }
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        LoggingSystem loggingSystem = beanFactory.getBean(LoggingApplicationListener.LOGGING_SYSTEM_BEAN_NAME, LoggingSystem.class);
        if (!beanFactory.containsBean(NacosLoggingContext.BEAN_NAME)) {
            beanFactory.registerSingleton(NacosLoggingContext.BEAN_NAME, new NacosLoggingContext(loggingSystem));
        }
        if (null == nacosLoggingContext) {
            nacosLoggingContext = (NacosLoggingContext) beanFactory.getBean(NacosLoggingContext.BEAN_NAME);
        }
        this.firstLoadNacosLoggingConfig(applicationContext.getEnvironment());
    }

    private void firstLoadNacosLoggingConfig(ConfigurableEnvironment environment) {
        List<NacosPropertySource> nacosPropertySources = new ArrayList<>();
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource instanceof NacosPropertySource) {
                nacosPropertySources.add((NacosPropertySource) propertySource);
            }
        }
        if (CollectionUtils.isEmpty(nacosPropertySources)) {
            log.debug("No nacos configuration had been loaded.");
            return;
        }
        if (!isFirstLoad.compareAndSet(true, false)) {
            return;
        }
        for (NacosPropertySource nacosPropertySource : nacosPropertySources) {
            nacosLoggingContext.refreshLogLevel(nacosPropertySource.getSource());
        }
    }

    @Override
    public int getOrder() {
        return order;
    }
}
