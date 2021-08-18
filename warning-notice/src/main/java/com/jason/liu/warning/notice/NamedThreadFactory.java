package com.jason.liu.warning.notice;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: meng.liu
 * @date: 2021/4/21
 * TODO:
 */
public class NamedThreadFactory implements ThreadFactory {

    private final String namePrefix;

    private final AtomicInteger count = new AtomicInteger(0);

    public NamedThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName(namePrefix + "-" + count.incrementAndGet());
        return thread;
    }
}
