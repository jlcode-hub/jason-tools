package com.jason.liu.slide.window;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author: meng.liu
 * @date: 2021/3/23
 * TODO: 窗口单元
 */
public class Unit {

    /**
     * 数量
     */
    private long count;

    /**
     * 单元的其实时间戳
     */
    private long unitStartTimestamp;

    private final Lock readLock;

    private final Lock writeLock;

    public Unit(long timestamp) {
        this.unitStartTimestamp = timestamp;
        this.count = 0;
        ReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    public Count getCount() {
        this.readLock.lock();
        try {
            return new Count(this.unitStartTimestamp, this.count);
        } finally {
            this.readLock.unlock();
        }
    }

    /**
     * 增加
     *
     * @param count
     */
    protected void increase(long count) {
        this.writeLock.lock();
        try {
            this.count += count;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 重置并增加
     *
     * @param timestamp
     */
    protected void reset(long timestamp) {
        this.writeLock.lock();
        try {
            this.unitStartTimestamp = timestamp;
            this.count = 0;
        } finally {
            writeLock.unlock();
        }
    }
}
