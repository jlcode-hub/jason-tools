package com.jason.liu.counter;


import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author: meng.liu
 * @date: 2021/1/7
 * TODO:
 */
@Slf4j
public class CounterInterceptor implements MethodInterceptor {

    public static final String BEAN_NAME = "JasonToolsCustom$$countInterceptor";

    private Counters counters;

    public CounterInterceptor(Counters counters) {
        this.counters = counters;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        try {
            Object returnedObject = methodInvocation.proceed();
            Method method = methodInvocation.getMethod();
            MethodCounter methodCounter = counters.get(method);
            if (null == methodCounter) {
                return returnedObject;
            }
            ICountRule rule;
            ICounter counter;
            if (null == (rule = methodCounter.getRule()) || null == (counter = methodCounter.getCounter())) {
                String errorInfo = "cannot find CountRule or Counter for the method " + methodCounter.getMethod().getName() + " of the class " + methodCounter.getTargetClass().getName();
                throw new IllegalStateException(errorInfo);
            }
            if (!rule.isSuccess(returnedObject)) {
                return returnedObject;
            }
            Object[] args = methodInvocation.getArguments();
            Object keyVal = methodCounter.getValueExpression().apply(args);
            if (null == keyVal) {
                log.warn("parse the count key {} result is null, method {}.{}", methodCounter.getCount().key(), methodCounter.getTargetClass().getName(), methodCounter.getMethod().getName());
                return returnedObject;
            }
            String key = String.valueOf(keyVal);
            long count = rule.getCount(key, args);
            if (0 == count) {
                return returnedObject;
            }
            counter.increase(String.valueOf(key), rule.getCount(key, args));
            return returnedObject;
        } catch (Exception e) {
            throw e;
        }
    }

}
