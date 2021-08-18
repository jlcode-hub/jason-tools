package com.jason.liu.counter;

import com.jason.liu.distributed.lock.DistributedLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author: meng.liu
 * @date: 2021/1/8
 * TODO:
 */
@Configuration
@EnableConfigurationProperties(CounterProperties.class)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class CounterConfiguration implements ImportAware {

    private AnnotationAttributes annotationAttributes;

    @Autowired
    private CounterProperties counterProperties;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.annotationAttributes = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableCounter.class.getName()));
    }

    @Bean(name = CounterAdvisor.BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CounterAdvisor counterAdvisor(CounterInterceptor counterInterceptor,
                                         Counters counters) {
        if (null != this.annotationAttributes) {
            CounterAdvisor counterAdvisor = new CounterAdvisor(this.annotationAttributes.getStringArray("basePackages"), counters);
            counterAdvisor.setAdviceBeanName(CounterAdvisor.BEAN_NAME);
            counterAdvisor.setAdvice(counterInterceptor);
            return counterAdvisor;
        } else {
            return new CounterAdvisor(new String[]{}, counters);
        }
    }

    @Bean(name = CounterInterceptor.BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CounterInterceptor countInterceptor(Counters counters) {
        return new CounterInterceptor(counters);
    }

    @Bean(name = Counters.BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Counters counters() {
        return new Counters();
    }

    @Bean(name = CounterFlushWorker.BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CounterFlushWorker counterFlushWorker(Counters counters,
                                                 DistributedLock distributedLock) {
        String name = this.annotationAttributes.getString("name");
        return new CounterFlushWorker(name, counters, distributedLock, counterProperties);
    }
}
