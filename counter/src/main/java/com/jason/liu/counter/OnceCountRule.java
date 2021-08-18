package com.jason.liu.counter;

import java.lang.reflect.Method;

/**
 * @author: meng.liu
 * @date: 2021/1/18
 * TODO: 单次计数处理器
 */
public abstract class OnceCountRule implements ICountRule {

    private Class targetClass;

    private Method method;

    @Override
    public void setTargetClass(Class clazz) {
        this.targetClass = targetClass;
    }

    @Override
    public void setTargetMethod(Method method) {
        this.method = method;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public Method getTargetMethod() {
        return method;
    }

    @Override
    public long getCount(String key, Object[] object) {
        return 1;
    }
}
