package com.jason.liu.counter;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

/**
 * @author: meng.liu
 * @date: 2021/1/7
 * TODO:
 */
public class CounterAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    public static final String BEAN_NAME = "JasonToolsCustom$$counterAdvisor";

    private String[] basePackages;

    private Counters counters;

    public CounterAdvisor(String[] basePackages, Counters counters) {
        this.basePackages = basePackages;
        this.counters = counters;
    }

    @Override
    public Pointcut getPointcut() {
        CounterClassFilter consumeClassFilter = new CounterClassFilter(basePackages);
        return new CounterPointcut(consumeClassFilter, counters);
    }
}
