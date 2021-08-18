package com.jason.liu.time.statistics;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author: meng.liu
 * @date: 2021/1/13
 * TODO: 统计汇总
 */
@Getter
@Builder
public class StatisticSummary implements Serializable {

    private static final StatisticSummary EMPTY = new StatisticSummary(0, 0, 0, 0, 0) {
        @Override
        public boolean isEmpty() {
            return true;
        }
    };

    public static StatisticSummary empty() {
        return EMPTY;
    }

    /**
     * 最小耗时
     */
    private long minTimeConsume;
    /**
     * 最最大耗时
     */
    private long maxTimeConsume;
    /**
     * 总耗时
     */
    private long totalTimeConsume;
    /**
     * 总调用次数
     */
    private long calledTimes;
    /**
     * 成功次数
     */
    private long successTimes;

    public boolean isEmpty() {
        return false;
    }
}
