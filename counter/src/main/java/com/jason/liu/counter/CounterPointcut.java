package com.jason.liu.counter;

import com.jason.liu.counter.utils.Holder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * @author: meng.liu
 * @date: 2021/1/7
 * TODO:
 */
@Slf4j
public class CounterPointcut extends StaticMethodMatcherPointcut {

    private Counters counters;

    private CounterClassFilter counterClassFilter;

    public CounterPointcut(CounterClassFilter consumeClassFilter, Counters counters) {
        this.counterClassFilter = consumeClassFilter;
        this.counters = counters;
        this.setClassFilter(consumeClassFilter);
    }

    @Override
    public boolean matches(Method method, Class<?> clazz) {
        boolean support = this.matcherInvoke(method, clazz);
        if (log.isTraceEnabled()) {
            log.trace("method {} in class {} match result: {}",
                    method.getName(),
                    clazz.getSimpleName(),
                    support);
        }
        return support;
    }

    private boolean matcherInvoke(Method method, Class clazz) {
        if (this.counterClassFilter.checkExcludeByName(clazz.getName())) {
            return false;
        }
        if (!this.counterClassFilter.matches(clazz)) {
            return false;
        }
        if (null != counters.get(method)) {
            return true;
        }
        Holder<Count> countHolder = new Holder<>();
        this.findAnnotation(countHolder, clazz, method.getName(), method.getParameterTypes());
        if (null != countHolder.getValue()) {
            counters.register(new MethodCounter()
                    .setTargetClass(clazz)
                    .setMethod(method)
                    .setCount(countHolder.getValue()));
            return true;
        } else {
            return false;
        }
    }

    private void findAnnotation(Holder<Count> counterHolder, Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        if (!clazz.isInterface() && null != clazz.getSuperclass()) {
            this.findAnnotation(counterHolder, clazz.getSuperclass(), methodName, paramTypes);
            if (null != counterHolder.getValue()) {
                return;
            }
        }
        for (Class<?> cInterface : clazz.getInterfaces()) {
            this.findAnnotation(counterHolder, cInterface, methodName, paramTypes);
            if (null != counterHolder.getValue()) {
                return;
            }
        }
        if (this.counterClassFilter.checkByClassName(clazz)) {
            for (Method declaredMethod : clazz.getDeclaredMethods()) {
                if (this.methodMatch(methodName, declaredMethod, paramTypes)) {
                    Count anno = declaredMethod.getAnnotation(Count.class);
                    if (null != anno) {
                        counterHolder.setValue(anno);
                        return;
                    }
                }
            }
        }
    }

    private boolean methodMatch(String name, Method method, Class<?>[] paramTypes) {
        if (!name.equals(method.getName())) {
            return false;
        }
        Class<?>[] ps = method.getParameterTypes();
        if (ps.length != paramTypes.length) {
            return false;
        }
        for (int i = 0; i < ps.length; i++) {
            if (!ps[i].equals(paramTypes[i])) {
                return false;
            }
        }
        return true;
    }
}
