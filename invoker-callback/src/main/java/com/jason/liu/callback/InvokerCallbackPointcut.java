package com.jason.liu.callback;

import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * @author: meng.liu
 * @date: 2021/4/21
 * TODO:
 */
public class InvokerCallbackPointcut extends StaticMethodMatcherPointcut {

    private InvokerCallbackClassFilter invokerCallbackClassFilter;

    private InvokerCallbackMethodCaches methodCaches;

    public InvokerCallbackPointcut(InvokerCallbackClassFilter invokerCallbackClassFilter,
                                   InvokerCallbackMethodCaches methodCaches) {
        this.invokerCallbackClassFilter = invokerCallbackClassFilter;
        this.methodCaches = methodCaches;
        this.setClassFilter(invokerCallbackClassFilter);
    }


    @Override
    public boolean matches(Method method, Class<?> clazz) {
        return this.matcherInvoke(method, clazz);
    }

    private boolean matcherInvoke(Method method, Class clazz) {
        if (this.invokerCallbackClassFilter.checkExcludeByName(clazz.getName())) {
            return false;
        }
        if (!this.invokerCallbackClassFilter.matches(clazz)) {
            return false;
        }
        if (null != methodCaches.get(method)) {
            return true;
        }
        InvokerCallback invokerCallback = this.findAnnotation(clazz, method.getName(), method.getParameterTypes());
        if (null != invokerCallback) {
            methodCaches.put(method, invokerCallback);
            return true;
        } else {
            return false;
        }
    }

    private InvokerCallback findAnnotation(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        InvokerCallback invokerCallback;
        if (!clazz.isInterface() && null != clazz.getSuperclass()) {
            invokerCallback = this.findAnnotation(clazz.getSuperclass(), methodName, paramTypes);
            if (null != invokerCallback) {
                return invokerCallback;
            }
        }
        for (Class<?> cInterface : clazz.getInterfaces()) {
            invokerCallback = this.findAnnotation(cInterface, methodName, paramTypes);
            if (null != invokerCallback) {
                return invokerCallback;
            }
        }
        if (this.invokerCallbackClassFilter.checkByClassName(clazz)) {
            for (Method declaredMethod : clazz.getDeclaredMethods()) {
                if (this.methodMatch(methodName, declaredMethod, paramTypes)) {
                    invokerCallback = declaredMethod.getAnnotation(InvokerCallback.class);
                    if (null != invokerCallback) {
                        return invokerCallback;
                    }
                }
            }
        }
        return null;
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
