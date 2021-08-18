package com.jason.liu.time.consume;

import com.jason.liu.time.utils.Holder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * @author: meng.liu
 * @date: 2021/1/7
 * TODO:
 */
@Slf4j
public class TimeConsumePointcut extends StaticMethodMatcherPointcut {

    private TimeConsumers timeConsumers;

    private TimeConsumeClassFilter timeConsumeClassFilter;

    public TimeConsumePointcut(TimeConsumeClassFilter consumeClassFilter, TimeConsumers timeConsumers) {
        this.timeConsumeClassFilter = consumeClassFilter;
        this.timeConsumers = timeConsumers;
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
        if (this.timeConsumeClassFilter.checkExcludeByName(clazz.getName())) {
            return false;
        }
        if (!this.timeConsumeClassFilter.matches(clazz)) {
            return false;
        }
        if (null != timeConsumers.get(method)) {
            return true;
        }
        Holder<TimeConsume> timeConsumeHolder = new Holder<>();
        this.findAnnotation(timeConsumeHolder, clazz, method.getName(), method.getParameterTypes());
        if (null != timeConsumeHolder.getValue()) {
            timeConsumers.put(method, clazz, timeConsumeHolder.getValue());
            return true;
        } else {
            return false;
        }
    }

    private void findAnnotation(Holder<TimeConsume> timeConsumeHolder, Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        if (!clazz.isInterface() && null != clazz.getSuperclass()) {
            this.findAnnotation(timeConsumeHolder, clazz.getSuperclass(), methodName, paramTypes);
            if (null != timeConsumeHolder.getValue()) {
                return;
            }
        }
        for (Class<?> cInterface : clazz.getInterfaces()) {
            this.findAnnotation(timeConsumeHolder, cInterface, methodName, paramTypes);
            if (null != timeConsumeHolder.getValue()) {
                return;
            }
        }
        if (this.timeConsumeClassFilter.checkByClassName(clazz)) {
            for (Method declaredMethod : clazz.getDeclaredMethods()) {
                if (this.methodMatch(methodName, declaredMethod, paramTypes)) {
                    TimeConsume anno = declaredMethod.getAnnotation(TimeConsume.class);
                    if (null != anno) {
                        timeConsumeHolder.setValue(anno);
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
