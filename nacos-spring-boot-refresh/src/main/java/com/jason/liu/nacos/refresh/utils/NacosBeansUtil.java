package com.jason.liu.nacos.refresh.utils;

import com.alibaba.nacos.spring.util.NacosBeanUtils;
import com.jason.liu.nacos.refresh.NacosLoggerAutoRefreshProcessor;
import com.jason.liu.nacos.refresh.binder.NacosRefreshScopeBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * @author: meng.liu
 * @date: 2020/8/5
 * TODO:
 */
public class NacosBeansUtil {

    public static void registerNacosRefreshScopeBeanPostProcessor(
            BeanDefinitionRegistry registry) {
        NacosBeanUtils.registerInfrastructureBeanIfAbsent(registry,
                NacosRefreshScopeBeanPostProcessor.BEAN_NAME,
                NacosRefreshScopeBeanPostProcessor.class);
    }

    public static void registerNacosLoggrRefreshProcessor(
            BeanDefinitionRegistry registry) {
        NacosBeanUtils.registerInfrastructureBeanIfAbsent(registry,
                NacosLoggerAutoRefreshProcessor.BEAN_NAME,
                NacosLoggerAutoRefreshProcessor.class);
    }
}
