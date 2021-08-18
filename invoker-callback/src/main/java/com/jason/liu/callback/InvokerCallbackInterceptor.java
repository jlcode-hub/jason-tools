package com.jason.liu.callback;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author: meng.liu
 * @date: 2021/4/21
 * TODO:
 */
@Slf4j
public class InvokerCallbackInterceptor implements MethodInterceptor {

    public static final String BEAN_NAME = "JasonToolsCustom$$invokerCallbackInterceptor";

    private InvokerCallbackMethodCaches methodCaches;

    private InvokerCallbackManager invokerCallbackManager;

    public InvokerCallbackInterceptor(InvokerCallbackMethodCaches methodCaches,
                                      InvokerCallbackManager invokerCallbackManager) {
        this.methodCaches = methodCaches;
        this.invokerCallbackManager = invokerCallbackManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            Object obj = invocation.proceed();
            invokerCallbackManager.executeCallback();
            return obj;
        } catch (Exception e) {
            Method method = invocation.getMethod();
            InvokerCallback callback = methodCaches.get(method);
            for (Class<? extends Throwable> aClass : callback.noInvokeFor()) {
                if (aClass.isAssignableFrom(e.getClass())) {
                    throw e;
                }
            }
            invokerCallbackManager.executeCallback();
            throw e;
        } finally {
            InvokerCallbackRegister.clear();
        }
    }
}
