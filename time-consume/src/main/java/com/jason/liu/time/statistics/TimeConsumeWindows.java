package com.jason.liu.time.statistics;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: meng.liu
 * @date: 2021/1/13
 * TODO:
 */
public class TimeConsumeWindows {

    public static final String BEAN_NAME = "JasonToolsCustom$$timeConsumeWindows";

    private final Map<String, TimeConsumeWindow> windows = new HashMap<>();

    public synchronized void register(String key, int period, int block) {
        if (windows.containsKey(key)) {
            return;
        }
        windows.put(key, new TimeConsumeWindow(key, period, block));
    }

    public TimeConsumeWindow get(String key) {
        return windows.get(key);
    }

    public Map<String, StatisticSummary> windowStatistics() {
        Map<String, StatisticSummary> windowStatistics = new HashMap<>();
        for (TimeConsumeWindow window : windows.values()) {
            windowStatistics.put(window.getKey(), window.summary());
        }
        return windowStatistics;
    }

}
