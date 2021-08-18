package com.jason.liu.callback;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * @author: meng.liu
 * @date: 2021/4/21
 * TODO:
 */
@Data
@ConfigurationProperties(prefix = "jason.tools.invoker-callback")
public class InvokerCallbackProperties {

    private ThreadPool threadPool = new ThreadPool();

    @Data
    public static class ThreadPool {
        /**
         * 最大空闲数
         */
        private int corePoolSize = 4;
        /**
         * 最大活跃数
         */
        private int maximumPoolSize = 4;
        /**
         * 队列大小
         */
        private int keepAliveTime = 60;
        /**
         * 最大空闲等待时长
         */
        private TimeUnit unit = TimeUnit.SECONDS;
        /**
         * 队列大小
         */
        private int queueSize = 1024;

    }

}
