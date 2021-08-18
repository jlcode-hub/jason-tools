package com.jason.liu.env.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-07-06 10:38:04
 * @todo
 */
@Slf4j
public class AdapterBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

    private String configEnv;

    public AdapterBeanDefinitionScanner(BeanDefinitionRegistry registry, String configEnv) {
        super(registry, false);
        this.configEnv = configEnv;
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> registryAdapters = super.doScan(basePackages);
        if (!CollectionUtils.isEmpty(registryAdapters)) {
            for (BeanDefinitionHolder registryAdapter : registryAdapters) {
                log.info("[Adapter]:register {} for environment [{}] ", registryAdapter.getBeanName(), configEnv);
            }
        }
        return registryAdapters;
    }
}
