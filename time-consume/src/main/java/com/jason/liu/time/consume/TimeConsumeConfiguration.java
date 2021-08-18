package com.jason.liu.time.consume;

import com.jason.liu.time.statistics.StatisticProperties;
import com.jason.liu.time.statistics.StatisticWorker;
import com.jason.liu.time.statistics.TimeConsumeWindows;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author: meng.liu
 * @date: 2021/1/8
 * TODO:
 */
@Configuration
@EnableConfigurationProperties(StatisticProperties.class)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@ConditionalOnProperty(prefix = "jason.tools.time-consume", name = "enabled", havingValue = "true", matchIfMissing = false)
public class TimeConsumeConfiguration implements ImportAware, EnvironmentAware {

    private AnnotationAttributes annotationAttributes;

    private Environment environment;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.annotationAttributes = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableTimeConsume.class.getName()));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Bean(name = TimeConsumeAdvisor.BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TimeConsumeAdvisor timeConsumeAdvisor(TimeConsumeInterceptor timeConsumeInterceptor,
                                                 TimeConsumers timeConsumers) {
        if (null != this.annotationAttributes) {
            TimeConsumeAdvisor timeConsumeAdvisor = new TimeConsumeAdvisor(this.annotationAttributes.getStringArray("basePackages"), timeConsumers);
            timeConsumeAdvisor.setAdviceBeanName(TimeConsumeAdvisor.BEAN_NAME);
            timeConsumeAdvisor.setOrder(this.annotationAttributes.getNumber("order"));
            timeConsumeAdvisor.setAdvice(timeConsumeInterceptor);
            return timeConsumeAdvisor;
        } else {
            return new TimeConsumeAdvisor(new String[]{}, timeConsumers);
        }
    }

    @Bean(name = TimeConsumeInterceptor.BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TimeConsumeInterceptor timeConsumeInterceptor(TimeConsumers timeConsumers) {
        return new TimeConsumeInterceptor(timeConsumers);
    }

    @Bean(name = TimeConsumeWindows.BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TimeConsumeWindows timeConsumeWindows() {
        return new TimeConsumeWindows();
    }

    @Bean(name = TimeConsumers.BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TimeConsumers timeConsumers(TimeConsumeWindows timeConsumeWindows, StatisticProperties statisticProperties) {
        return new TimeConsumers(timeConsumeWindows, statisticProperties, environment);
    }

    @Bean(name = StatisticWorker.BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public StatisticWorker statisticWorker(TimeConsumers timeConsumers) {
        return new StatisticWorker(timeConsumers);
    }
}
