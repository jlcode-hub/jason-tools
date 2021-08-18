package com.jason.liu.counter;

import java.util.Map;

/**
 * @author: meng.liu
 * @date: 2021/1/18
 * TODO: 计数器
 */
public interface ICounter {

    /**
     * 增加计数，该方法用于在内存或者redis中先进行计数，以提升性能
     * 该方法用于调用业务接口时计数统计，会在业务方法调用时进行同步调用，将直接影响业务性能
     *
     * @param key
     * @param value
     */
    void increase(String key, long value);

    /**
     * 减少计数，该方法用于在内存或者redis中先进行计数，以提升性能
     * 该方法用于刷盘后更新计数值，注意该方法必须保证原子性
     *
     * @param key
     * @param value
     */
    void decrease(String key, long value);

    /**
     * 获取当前阶段的所有计数信息
     * 该方法用于数据库刷盘操作
     *
     * @return
     */
    Map<String, Long> all();

    /**
     * 冲刷计数到数据库
     *
     * @param key
     * @param value
     * @return 返回成功后才会调用decrease来修改
     */
    boolean flushCount(String key, long value);
}
