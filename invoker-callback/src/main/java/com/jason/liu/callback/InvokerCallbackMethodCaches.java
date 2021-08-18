package com.jason.liu.callback;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: meng.liu
 * @date: 2021/4/21
 * TODO:
 */
public class InvokerCallbackMethodCaches {

    public static final String BEAN_NAME = "JasonTools$InvokerCallbackMethodCaches";

    private Map<Method, InvokerCallback> methods = new ConcurrentHashMap<>();

    public void put(Method method, InvokerCallback callback) {
        methods.put(method, callback);
    }

    public InvokerCallback get(Method method) {
        return methods.get(method);
    }
}
