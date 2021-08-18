package com.jason.liu.counter;

import java.lang.reflect.Method;

/**
 * @author: meng.liu
 * @date: 2021/1/18
 * TODO:
 */
public interface ICountRule {
    /**
     * 业务类
     *
     * @param clazz
     */
    void setTargetClass(Class clazz);

    /**
     * 业务方法
     *
     * @param method
     */
    void setTargetMethod(Method method);

    /**
     * 获取调用该业务方法时的计数
     *
     * @param key
     * @param object
     * @return
     */
    long getCount(String key, Object[] object);

    /**
     * 用于判断请求是否
     *
     * @param object 接口返回参数
     * @return
     */
    boolean isSuccess(Object object);
}
