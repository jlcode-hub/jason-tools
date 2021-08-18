package com.jason.liu.slide.window;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: meng.liu
 * @date: 2021/3/23
 * TODO: 窗口
 */
@Slf4j
public class Window {

    /**
     * 窗口标识
     */
    private String key;

    /**
     * 窗口大小，单位:毫秒
     */
    private int windowSize;

    /**
     * 窗口的总时长
     */
    private int windowTime;

    /**
     * 计数单元时长，单位:毫秒
     * 该参数越小窗口将约平滑，但是性能损耗越大
     */
    private int blockTime;

    /**
     * 窗口数
     */
    private Unit[] units;

    /**
     * 单元偏移量
     */
    private int unitPos = 0;

    /**
     * 最后一次移动的时间戳
     */
    private long lastMoveTimestamp = 0;

    /**
     * 最后一次调用的时间戳
     */
    private long lastCalledTimestamp = 0;

    /**
     * 窗口初始化的时间戳
     */
    private long windowStartTimestamp = 0;

    /**
     * 滑动窗口
     *
     * @param key
     * @param period
     * @param block
     */
    public Window(String key, int period, int block) {
        this.key = key;
        this.blockTime = block * 1000;
        this.windowSize = Math.max(period / block, 1);
        this.windowTime = blockTime * windowSize;
        this.initWindow();
    }

    /**
     * 初始化窗口
     */
    private void initWindow() {
        this.lastMoveTimestamp = SystemClock.currentTimeMillis();
        this.windowStartTimestamp = this.lastMoveTimestamp;
        this.units = new Unit[this.windowSize];
        for (int pos = 0; pos < this.windowSize; pos++) {
            this.units[pos] = new Unit(this.windowStartTimestamp);
        }
    }

    private int position(long calledTimestamp) {
        return (int) ((calledTimestamp - windowStartTimestamp) / this.blockTime % this.windowSize);
    }

    /**
     * 计数
     */
    public void increase() {
        this.increase(1);
    }

    /**
     * 计数
     *
     * @param count
     */
    public void increase(long count) {
        try {
            long now = SystemClock.currentTimeMillis();
            int currentPos = this.position(now);
            if (currentPos != this.unitPos || now - this.lastMoveTimestamp > this.windowTime) {
                synchronized (this) {
                    //todo 滑动当前窗口
                    if (currentPos != this.unitPos || now - this.lastMoveTimestamp > this.windowTime) {
                        //todo 重置窗口块  开始时间+块偏移量+窗口滑动次数*窗口大小
                        this.lastMoveTimestamp = this.windowStartTimestamp + currentPos * this.blockTime + this.slideNumber(now) * windowTime;
                        this.units[currentPos].reset(this.lastMoveTimestamp);
                        this.unitPos = currentPos;
                    }
                }
            }
            this.units[currentPos].increase(count);
            this.lastCalledTimestamp = now;
        } catch (Exception e) {
            //no thing
            if (log.isDebugEnabled()) {
                log.warn("", e);
            }
        }
    }

    /**
     * 从开始到指定时间总共的滑动次数
     */
    private long slideNumber(long timestamp) {
        return (timestamp - this.windowStartTimestamp) / windowTime;
    }

    /**
     * 获取窗口数据
     *
     * @return
     */
    public StatisticSummary summary() {
        //todo 锁住当前对象， 防止窗口滑动
        Count[] snapshot = new Count[this.units.length];
        //todo 锁住当前对象，获取参数快照
        synchronized (this) {
            for (int i = 0; i < this.units.length; i++) {
                snapshot[i] = this.units[i].getCount();
            }
        }
        long minValid = SystemClock.currentTimeMillis() - this.windowTime;
        StatisticSummary statisticSummary = new StatisticSummary(this.key);
        for (final Count count : snapshot) {
            if (count.getTimestamp() < minValid) {
                continue;
            }
            statisticSummary.ins(count.getCount());
        }
        statisticSummary.setStatisticTime(this.windowTime);
        return statisticSummary;
    }
}
