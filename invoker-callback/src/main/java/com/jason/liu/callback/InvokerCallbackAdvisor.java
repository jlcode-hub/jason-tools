package com.jason.liu.callback;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

/**
 * @author: meng.liu
 * @date: 2021/4/21
 * TODO:
 */
public class InvokerCallbackAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    public static final String BEAN_NAME = "JasonTools$InvokerCallbackAdvisor";

    private String[] basePackages;

    private InvokerCallbackMethodCaches methodCaches;

    public InvokerCallbackAdvisor(String[] basePackages,
                                  InvokerCallbackMethodCaches methodCaches) {
        this.basePackages = basePackages;
        this.methodCaches = methodCaches;
    }

    @Override
    public Pointcut getPointcut() {
        InvokerCallbackClassFilter invokerCallbackClassFilter = new InvokerCallbackClassFilter(this.basePackages);
        return new InvokerCallbackPointcut(invokerCallbackClassFilter, methodCaches);
    }
}
