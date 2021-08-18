package com.jason.liu.time.consume;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

/**
 * @author: meng.liu
 * @date: 2021/1/7
 * TODO:
 */
public class TimeConsumeAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    public static final String BEAN_NAME = "JasonToolsCustom$$timeConsumeAdvisor";

    private String[] basePackages;

    private TimeConsumers timeConsumers;

    public TimeConsumeAdvisor(String[] basePackages, TimeConsumers timeConsumers) {
        this.basePackages = basePackages;
        this.timeConsumers = timeConsumers;
    }

    @Override
    public Pointcut getPointcut() {
        TimeConsumeClassFilter consumeClassFilter = new TimeConsumeClassFilter(basePackages);
        return new TimeConsumePointcut(consumeClassFilter, timeConsumers);
    }
}
