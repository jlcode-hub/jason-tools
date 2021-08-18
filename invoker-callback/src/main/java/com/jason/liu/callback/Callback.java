package com.jason.liu.callback;

/**
 * @author: meng.liu
 * @date: 2021/4/21
 * TODO: 回调接口类，回调存储使用LinkedHashSet进行存储，调用方可以通过继承该类来实现回调去重
 */
@FunctionalInterface
public interface Callback {

    /**
     * 回调函数
     * 当该函数返回false或则抛出异常时，将结束调用
     *
     * @return
     * @throws Exception
     */
    boolean callback() throws Exception;


}
