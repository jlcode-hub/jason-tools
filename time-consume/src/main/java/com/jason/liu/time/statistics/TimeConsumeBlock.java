package com.jason.liu.time.statistics;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author: meng.liu
 * @date: 2021/1/13
 * TODO:
 */
@Getter
public class TimeConsumeBlock {

    private long blockStart;
    /**
     * 窗口调用次数
     */
    private AtomicInteger times;
    /**
     * 成功次数
     */
    private AtomicInteger successTimes;
    /**
     * 总耗时窗口
     */
    private List<Long> timeConsumes;

    private ReadWriteLock lock;

    private Lock readLock;

    private Lock writeLock;

    public TimeConsumeBlock(long blockStart) {
        this.blockStart = blockStart;
        this.times = new AtomicInteger(0);
        this.successTimes = new AtomicInteger(0);
        this.timeConsumes = new ArrayList<>(1024);
        this.lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    public void reset(long start) {
        writeLock.lock();
        try {
            times.set(0);
            successTimes.set(0);
            timeConsumes.clear();
            this.blockStart = start;
        } finally {
            writeLock.unlock();
        }
    }

    public void update(long calledTime, boolean isSuccess, long timeConsume) {
        if (calledTime < this.blockStart) {
            return;
        }
        writeLock.lock();
        try {
            times.incrementAndGet();
            if (isSuccess) {
                successTimes.incrementAndGet();
            }
            timeConsumes.add(timeConsume);
        } finally {
            writeLock.unlock();
        }
    }
}
