package com.jason.liu.slide.window;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: meng.liu
 * @date: 2021/1/13
 * TODO:
 */
public class SystemClock {

    private final AtomicLong currentTimeMillis;

    private static final SystemClock CLOCK = new SystemClock();

    private SystemClock() {
        currentTimeMillis = new AtomicLong(System.currentTimeMillis());
        scheduleClockUpdating();
    }


    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(
                runnable -> {
                    Thread thread = new Thread(runnable, "JasonTools-System-Clock");
                    thread.setDaemon(true);
                    return thread;

                });
        scheduler.scheduleAtFixedRate(() -> currentTimeMillis.set(System.currentTimeMillis()), 1, 1, TimeUnit.MILLISECONDS);
    }

    public static long currentTimeMillis() {
        return CLOCK.currentTimeMillis.get();
    }

}
