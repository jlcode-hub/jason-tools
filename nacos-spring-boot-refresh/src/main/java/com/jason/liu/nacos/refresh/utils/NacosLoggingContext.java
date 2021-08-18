package com.jason.liu.nacos.refresh.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: meng.liu
 * @date: 2021/2/3
 * TODO:
 */
public class NacosLoggingContext {

    private static final Logger log = LoggerFactory.getLogger(NacosLoggingContext.class);

    public static final String LOGGER_TAG = "logging.level.";

    public static final String BEAN_NAME = "JasonTools$$$$NacosLoggingContext";

    private LoggingSystem loggingSystem;

    public NacosLoggingContext(LoggingSystem loggingSystem) {
        this.loggingSystem = loggingSystem;
    }

    /**
     * 刷新日志配置，该方法会重置原来的配置
     *
     * @param properties
     */
    public synchronized void refreshLogLevel(Map<String, Object> properties) {
        Map<String, LogLevel> levelConfig = new LinkedHashMap<>();
        for (Object t : properties.keySet()) {
            String key = String.valueOf(t);
            if (key.startsWith(NacosLoggingContext.LOGGER_TAG)) {
                LogLevel level = LogLevelUtils.getLevel(String.valueOf(properties.get(key)), LogLevel.INFO);
                levelConfig.put(key.replace(NacosLoggingContext.LOGGER_TAG, ""), level);
            }
        }
        levelConfig.forEach((path, level) -> {
            loggingSystem.setLogLevel(path, level);
            log.info("Log level set: {} => {}", path, level);
        });
    }
}
