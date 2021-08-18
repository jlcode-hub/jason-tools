package com.jason.liu.time.statistics;

import com.jason.liu.time.consume.MethodInfo;
import com.jason.liu.time.consume.TimeConsumers;
import com.jason.liu.time.utils.TimeLogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author: meng.liu
 * @date: 2021/1/12
 * TODO:
 */
@Slf4j
public class StatisticWorker implements InitializingBean {

    public static final String BEAN_NAME = "JasonToolsCustom$$statisticWorker";

    private static final String PRINTER_THREAD_NAME = "JasonToolsCustom$$timeConsumePrinter";

    private TimeConsumers timeConsumers;

    private StatisticProperties statisticProperties;

    private long printInterval;

    private boolean running = false;

    private String title;

    public StatisticWorker(TimeConsumers timeConsumers) {
        this.timeConsumers = timeConsumers;
        this.statisticProperties = timeConsumers.getStatisticProperties();
        this.printInterval = this.statisticProperties.getStatisticPrintInterval();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.statisticProperties.getEnabled() && this.statisticProperties.getPrintWindow()) {
            this.startPrinter();
        }
    }

    private void startPrinter() {
        synchronized (this) {
            if (running) {
                return;
            }
            Executors.newSingleThreadScheduledExecutor(run -> {
                Thread thread = new Thread(run, PRINTER_THREAD_NAME);
                thread.setDaemon(true);
                return thread;
            }).scheduleWithFixedDelay(this::printTimeConsume, 5, this.printInterval, TimeUnit.SECONDS);
            running = true;
        }
    }

    private void printTimeConsume() {
        Map<String, StatisticSummary> summaries = this.timeConsumers.getTimeConsumeWindows().windowStatistics();
        StringBuilder builder = new StringBuilder();
        if (null == title) {
            this.title = TimeLogUtils.statisticTitle(this.timeConsumers.getMaxClassLen(), this.timeConsumers.getMaxMethodLen());
        }
        TimeLogUtils.printTitle(builder, this.title);
        summaries.forEach((key, summary) -> {
            MethodInfo methodInfo = this.timeConsumers.get(key);
            if (null == methodInfo) {
                return;
            }
            if (summary.isEmpty()) {
                TimeLogUtils.printEmptyStatisticInfo(builder, this.timeConsumers.getMaxClassLen(), this.timeConsumers.getMaxMethodLen(), methodInfo);
            } else {
                TimeLogUtils.printStatisticInfo(builder, this.timeConsumers.getMaxClassLen(), this.timeConsumers.getMaxMethodLen(), methodInfo, summary);
            }
            TimeLogUtils.printSepLine(builder, this.title);
        });
        log.info("\n{}", builder);
    }
}
