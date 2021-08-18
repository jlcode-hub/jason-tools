package com.jason.liu.counter.utils;

/**
 * @author: meng.liu
 * @date: 2021/1/8
 * TODO:
 */
public class Holder<T> {

    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
