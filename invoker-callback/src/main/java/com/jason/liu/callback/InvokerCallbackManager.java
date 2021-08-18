package com.jason.liu.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: meng.liu
 * @date: 2021/4/21
 * TODO:
 */
@Slf4j
public class InvokerCallbackManager implements InitializingBean {

    public static final String BEAN_NAME = "JasonTools$InvokerCallbackManager";

    private InvokerCallbackProperties.ThreadPool threadPool;

    private Executor executor;

    public InvokerCallbackManager(InvokerCallbackProperties.ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    /**
     * 执行回调
     */
    public void executeCallback() {
        log.debug("start execute invoker callbacks...");
        this.executeAsync();
        this.execute();
        log.debug("execute invoker completed.");
    }

    private void execute() {
        this.invoke(InvokerCallbackRegister.get());
    }

    private void executeAsync() {
        try {
            this.executor.execute(() -> this.invoke(InvokerCallbackRegister.getAsync()));
        } catch (RejectedExecutionException e) {
            log.error("execute async callback failed, execute rejected.", e);
        }
    }

    private void invoke(LinkedHashMap<String, LinkedHashSet<Callback>> groupCallbacks) {
        if (CollectionUtils.isEmpty(groupCallbacks)) {
            return;
        }
        for (Map.Entry<String, LinkedHashSet<Callback>> entry : groupCallbacks.entrySet()) {
            LinkedHashSet<Callback> callbacks = entry.getValue();
            if (CollectionUtils.isEmpty(callbacks)) {
                continue;
            }
            try {
                for (Callback callback : callbacks) {
                    if (!callback.callback()) {
                        log.debug("execute callback returned false, stop the execution");
                        break;
                    }
                }
            } catch (Exception e) {
                log.warn("invoker callback exception, stop the execution.", e);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.initExecutor();
    }

    private void initExecutor() {
        executor = new ThreadPoolExecutor(threadPool.getCorePoolSize(),
                threadPool.getMaximumPoolSize(),
                threadPool.getKeepAliveTime(),
                threadPool.getUnit(),
                new ArrayBlockingQueue<>(threadPool.getQueueSize()),
                new NamedThreadFactory("Invoker-Callback-Pool"));
    }
}
