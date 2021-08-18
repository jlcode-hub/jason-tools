package com.jason.liu.callback;

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
 * @date: 2021/4/21
 * TODO:
 */
@Configuration
@EnableConfigurationProperties(InvokerCallbackProperties.class)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class InvokerCallbackConfiguration implements ImportAware {

    private AnnotationAttributes annotationAttributes;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.annotationAttributes = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableInvokerCallback.class.getName()));
    }

    @Bean(InvokerCallbackManager.BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public InvokerCallbackManager invokerCallbackManager(InvokerCallbackProperties properties) {
        return new InvokerCallbackManager(properties.getThreadPool());
    }

    @Bean(InvokerCallbackMethodCaches.BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public InvokerCallbackMethodCaches invokerCallbackMethodCaches() {
        return new InvokerCallbackMethodCaches();
    }

    @Bean(name = InvokerCallbackAdvisor.BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public InvokerCallbackAdvisor invokerCallbackAdvisor(InvokerCallbackInterceptor invokerCallbackInterceptor,
                                                         InvokerCallbackMethodCaches invokerCallbackMethodCaches) {
        if (null != this.annotationAttributes) {
            InvokerCallbackAdvisor invokerCallbackAdvisor = new InvokerCallbackAdvisor(this.annotationAttributes.getStringArray("basePackages"), invokerCallbackMethodCaches);
            invokerCallbackAdvisor.setAdviceBeanName(InvokerCallbackAdvisor.BEAN_NAME);
            invokerCallbackAdvisor.setOrder(this.annotationAttributes.getNumber("order"));
            invokerCallbackAdvisor.setAdvice(invokerCallbackInterceptor);
            return invokerCallbackAdvisor;
        } else {
            return new InvokerCallbackAdvisor(new String[]{}, invokerCallbackMethodCaches);
        }
    }

    @Bean(name = InvokerCallbackInterceptor.BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public InvokerCallbackInterceptor invokerCallbackInterceptor(InvokerCallbackMethodCaches invokerCallbackMethodCaches,
                                                                 InvokerCallbackManager invokerCallbackManager) {
        return new InvokerCallbackInterceptor(invokerCallbackMethodCaches, invokerCallbackManager);
    }

}
