package com.jason.liu.time.statistics;

import com.jason.liu.time.utils.SystemClock;
import lombok.Getter;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: meng.liu
 * @date: 2021/1/12
 * TODO:
 */
public class TimeConsumeWindow implements Serializable {
    /**
     * 标识
     */
    @Getter
    private String key;
    /**
     * 最后一次调用时间戳
     */
    private long startTimestamp;
    /**
     * 块时长
     */
    private long blockTime;
    /**
     * 窗口时长
     */
    private long windowTime;
    /**
     * 窗口位置
     */
    private AtomicInteger position = new AtomicInteger(0);
    /**
     * 窗口大小
     */
    private int windowSize;

    private TimeConsumeBlock[] blocks;
    /**
     * 最后一次移动的时间戳
     */
    private long lastMoveTimeStamp;
    /**
     * 最后一次移动的时间戳
     */
    private long lastCompletedTimestamp = 0;

    /**
     * @param key
     * @param period 周期（分钟）
     */
    public TimeConsumeWindow(String key, int period, int block) {
        this.key = key;
        this.blockTime = block * 1000;
        this.windowSize = Math.max(period / block, 1);
        this.windowTime = blockTime * windowSize;
        this.initWindow();
    }

    private void initWindow() {
        this.blocks = new TimeConsumeBlock[windowSize];
        this.startTimestamp = SystemClock.currentTimeMillis();
        for (int pos = 0; pos < windowSize; pos++) {
            this.blocks[pos] = new TimeConsumeBlock(this.startTimestamp);
        }
        this.lastMoveTimeStamp = this.startTimestamp;
    }

    private int position(long calledTimestamp) {
        return (int) ((calledTimestamp - startTimestamp) / this.blockTime % this.windowSize);
    }

    public void add(CallInfo callInfo) {
        long completeTimestamp = callInfo.getCompletedTimestamp();
        int callPos = this.position(completeTimestamp);
        long now = SystemClock.currentTimeMillis();
        int currentPos = this.position(now);
        int oldPosition = position.get();
        //todo 如果当前窗口位置不一致或者整个时长已经超过了一个完整的窗口
        if (currentPos != oldPosition || now - this.lastMoveTimeStamp > this.windowTime) {
            //todo 锁住当前对象，滑动窗口
            synchronized (this) {
                oldPosition = position.get();
                now = SystemClock.currentTimeMillis();
                currentPos = this.position(now);
                //todo 滑动当前窗口
                if (currentPos != oldPosition || now - this.lastMoveTimeStamp > this.windowTime) {
                    //todo 重置窗口块  开始时间+块偏移量+窗口滑动次数*窗口大小
                    this.lastMoveTimeStamp = startTimestamp + currentPos * this.blockTime + (now - startTimestamp) / windowTime * windowTime;
                    this.blocks[currentPos].reset(this.lastMoveTimeStamp);
                    this.position.set(currentPos);
                }
            }
        }
        this.blocks[callPos].update(completeTimestamp, callInfo.isSuccess(), callInfo.getTimeConsume());
        this.lastCompletedTimestamp = Math.max(callInfo.getCompletedTimestamp(), this.lastCompletedTimestamp);
    }

    /**
     * 获取窗口数据
     *
     * @return
     */
    public StatisticSummary summary() {
        //todo 锁住当前对象， 防止窗口滑动
        synchronized (this) {
            long minValid = SystemClock.currentTimeMillis() - this.windowTime;
            long minTimeConsume = Integer.MAX_VALUE;
            long maxTimeConsume = Integer.MIN_VALUE;
            long totalTimeConsume = 0;
            long calledTimes = 0;
            long successTimes = 0;
            for (final TimeConsumeBlock block : this.blocks) {
                if (block.getBlockStart() < minValid) {
                    continue;
                }
                block.getReadLock().lock();
                try {
                    if (block.getBlockStart() < minValid) {
                        continue;
                    }
                    for (Long timeConsume : block.getTimeConsumes()) {
                        minTimeConsume = Math.min(minTimeConsume, timeConsume);
                        maxTimeConsume = Math.max(maxTimeConsume, timeConsume);
                        totalTimeConsume += timeConsume;
                    }
                    calledTimes += block.getTimes().get();
                    successTimes += block.getSuccessTimes().get();
                } finally {
                    block.getReadLock().unlock();
                }
            }
            if (calledTimes == 0) {
                return StatisticSummary.empty();
            }
            return StatisticSummary.builder()
                    .minTimeConsume(minTimeConsume)
                    .maxTimeConsume(maxTimeConsume)
                    .totalTimeConsume(totalTimeConsume)
                    .calledTimes(calledTimes)
                    .successTimes(successTimes).build();
        }
    }

}
