package com.jason.liu.nacos.refresh.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.logging.LogLevel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author: meng.liu
 * @date: 2021/2/3
 * TODO:
 */
public class LogLevelUtils {

    static final Map<String, LogLevel> levelMap = new HashMap<>();

    static {
        for (LogLevel value : LogLevel.values()) {
            levelMap.put(value.name(), value);
        }
    }

    public static LogLevel getLevel(String level, LogLevel defaultLevel) {
        if (StringUtils.isBlank(level)) {
            return defaultLevel;
        }
        if ("false".equalsIgnoreCase(level)) {
            return LogLevel.OFF;
        }
        return Optional.ofNullable(levelMap.get(level.toUpperCase())).orElse(defaultLevel);
    }

    public static LogLevel getLevel(String level) {
        if (StringUtils.isBlank(level)) {
            return null;
        }
        if ("false".equalsIgnoreCase(level)) {
            return LogLevel.OFF;
        }
        return levelMap.get(level.toUpperCase());
    }
}
