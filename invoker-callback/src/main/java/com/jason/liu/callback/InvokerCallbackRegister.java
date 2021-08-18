package com.jason.liu.callback;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * @author: meng.liu
 * @date: 2021/4/21
 * TODO: 接口事务注册器，
 * 每个链可以使用名称标识
 */
public class InvokerCallbackRegister {

    public static final String GROUP_NAME = "_DefaultInvokerCallbackGroup_";

    private final static ThreadLocal<LinkedHashMap<String, LinkedHashSet<Callback>>> INVOKER_REGISTER = new ThreadLocal<>();

    private final static ThreadLocal<LinkedHashMap<String, LinkedHashSet<Callback>>> ASYNC_INVOKER_REGISTER = new ThreadLocal<>();

    /**
     * 注册一个异步执行的回调到新创建的执行段
     *
     * @param group
     * @param callback
     */
    public static void registerAsync(String group, Callback callback) {
        getOrCreate(group).add(callback);
    }

    /**
     * 注册一个异步执行的回调在当前执行段
     *
     * @param callback
     */
    public static void registerAsync(Callback callback) {
        registerAsync(GROUP_NAME, callback);
    }

    /**
     * 注册一个同步回调到新创建的执行段
     *
     * @param group
     * @param callback
     */
    public static void registerNew(String group, Callback callback) {
        getOrCreate(group).add(callback);
    }

    /**
     * 注册一个同步回调在当前执行段
     *
     * @param callback
     */
    public static void register(Callback callback) {
        registerNew(GROUP_NAME, callback);
    }

    static LinkedHashSet<Callback> getOrCreate(String group) {
        LinkedHashMap<String, LinkedHashSet<Callback>> linkedHashMap = INVOKER_REGISTER.get();
        if (null == linkedHashMap) {
            linkedHashMap = new LinkedHashMap<>();
            INVOKER_REGISTER.set(linkedHashMap);
        }
        return linkedHashMap.computeIfAbsent(group, k -> new LinkedHashSet<>());
    }

    static LinkedHashSet<Callback> getOrCreateAsync(String group) {
        LinkedHashMap<String, LinkedHashSet<Callback>> linkedHashMap = ASYNC_INVOKER_REGISTER.get();
        if (null == linkedHashMap) {
            linkedHashMap = new LinkedHashMap<>();
            ASYNC_INVOKER_REGISTER.set(linkedHashMap);
        }
        return linkedHashMap.computeIfAbsent(group, k -> new LinkedHashSet<>());
    }

    static LinkedHashMap<String, LinkedHashSet<Callback>> get() {
        return INVOKER_REGISTER.get();
    }

    static LinkedHashMap<String, LinkedHashSet<Callback>> getAsync() {
        return ASYNC_INVOKER_REGISTER.get();
    }

    static void clear() {
        INVOKER_REGISTER.remove();
        ASYNC_INVOKER_REGISTER.remove();
    }
}
