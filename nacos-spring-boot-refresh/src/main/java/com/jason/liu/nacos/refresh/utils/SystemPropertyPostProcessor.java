package com.jason.liu.nacos.refresh.utils;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author: meng.liu
 * @date: 2021/2/18
 * TODO:
 */
public class SystemPropertyPostProcessor implements EnvironmentPostProcessor {


    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        SystemProperty.setEnvironment(environment);
    }
}
