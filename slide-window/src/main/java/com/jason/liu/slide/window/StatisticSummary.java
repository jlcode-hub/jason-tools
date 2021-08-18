package com.jason.liu.slide.window;

import lombok.Data;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

/**
 * @author: meng.liu
 * @date: 2021/3/23
 * TODO:
 */
@Data
public class StatisticSummary {

    private String key;

    /**
     * 统计时长
     */
    private long statisticTime;

    /**
     * 总数
     */
    private long total = 0;

    public StatisticSummary(String key) {
        this.key = key;
    }

    public void ins(long count) {
        this.total += count;
    }

    public void setStatisticTime(long statisticTime) {
        this.statisticTime = statisticTime;
    }

    public Long getTime() {
        return TimeUnit.MILLISECONDS.toSeconds(statisticTime);
    }

    public String getTps() {
        long time = this.getTime();
        if (time == 0) {
            return total + "";
        }
        float num = (float) total / time;
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(num);

    }
}
