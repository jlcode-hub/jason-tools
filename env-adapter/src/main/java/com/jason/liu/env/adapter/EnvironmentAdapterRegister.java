package com.jason.liu.env.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-07-01 09:50:13
 * @todo
 */
@Slf4j
public class EnvironmentAdapterRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private final AtomicBoolean isRegistered = new AtomicBoolean(false);

    private AnnotationAttributes annotationAttributes;

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        this.annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableAdapter.class.getName()));
        if (this.isRegistered.compareAndSet(false, true)) {
            this.registryAdapter(registry);
        }
    }

    private void registryAdapter(BeanDefinitionRegistry registry) {
        String configEnv = this.environment.getProperty("environment.adapt.type", "default");
        AdapterBeanDefinitionScanner scanner = new AdapterBeanDefinitionScanner(registry, configEnv);
        scanner.setBeanNameGenerator(new AnnotationBeanNameGenerator());
        scanner.addIncludeFilter(new AnnotationTypeValueFilter<AdapterBean>(AdapterBean.class) {
            @Override
            protected boolean matchAnnotationMetadata(AdapterBean annotation) {
                return annotation.value().equals(configEnv);
            }
        });
        String[] scanBasePackages = this.annotationAttributes.getStringArray("scanBasePackages");
        if (scanBasePackages.length == 0) {
            throw new IllegalArgumentException("scanBasePackages for EnableAdapter must be configured.");
        }
        scanner.scan(scanBasePackages);
    }
}
