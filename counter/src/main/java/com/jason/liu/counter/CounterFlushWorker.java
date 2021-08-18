package com.jason.liu.counter;

import com.jason.liu.distributed.lock.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author: meng.liu
 * @date: 2021/1/18
 * TODO:
 */
@Slf4j
public class CounterFlushWorker implements ApplicationListener<ApplicationStartedEvent> {

    public static final String BEAN_NAME = "JasonToolsCustom$$counterFlushScheduled";

    private static final String COUNTER_FLUSH_SCHEDULED = "JasonToolsCustom$$counterFlushScheduled";

    private static final String LOCK_KEY = "JasonToolsCustom$$counterFlushScheduledWorker$$Lock";

    private final String lockKey;

    private DistributedLock distributedLock;

    private boolean running;

    private Counters counters;

    private CounterProperties counterProperties;

    public CounterFlushWorker(String name,
                              Counters counters,
                              DistributedLock distributedLock,
                              CounterProperties counterProperties) {
        this.lockKey = LOCK_KEY + "-" + name;
        this.distributedLock = distributedLock;
        this.counters = counters;
        this.counterProperties = counterProperties;
        this.running = false;
    }


    private void flush() {
        if (!distributedLock.lock(lockKey, counterProperties.getLockTime())) {
            return;
        }
        long start = System.currentTimeMillis();
        try {
            Collection<MethodCounter> methodCounters = counters.getAll();
            for (MethodCounter methodCounter : methodCounters) {
                ICounter counter = methodCounter.getCounter();
                if (null == methodCounter.getCounter()) {
                    log.warn("cannot find Counter for the method {} of the class {}", methodCounter.getMethod().getName(), methodCounter.getTargetClass().getName());
                    continue;
                }
                Map<String, Long> countInfo = counter.all();
                if (CollectionUtils.isEmpty(countInfo)) {
                    continue;
                }
                try {
                    countInfo.forEach((key, count) -> {
                        if (0 == count) {
                            return;
                        }
                        if (counter.flushCount(key, count)) {
                            counter.decrease(key, count);
                        } else {
                            log.warn("flush key {} failed for the method {} of the class {}", key, methodCounter.getMethod().getName(), methodCounter.getTargetClass().getName());
                        }
                    });
                } catch (Exception e) {
                    log.error("flush exception, cause: {}", e.getMessage());
                    if (log.isDebugEnabled()) {
                        log.error("", e);
                    }
                }
            }
        } finally {
            distributedLock.unLock(lockKey);
            if (log.isDebugEnabled()) {
                log.debug("flush count info take : {}ms", System.currentTimeMillis() - start);
            }
        }
    }


    private synchronized void startCounterFlushScheduled() {
        if (running) {
            return;
        }
        Executors.newSingleThreadScheduledExecutor(run -> {
            Thread thread = new Thread(run, COUNTER_FLUSH_SCHEDULED);
            thread.setDaemon(true);
            return thread;
        }).scheduleWithFixedDelay(this::flush, counterProperties.getFlushInterval(), counterProperties.getFlushInterval(), TimeUnit.SECONDS);
        running = true;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        this.startCounterFlushScheduled();
    }

}
