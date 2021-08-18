package com.jason.liu.time.consume;

import com.jason.liu.time.statistics.StatisticProperties;
import com.jason.liu.time.statistics.TimeConsumeWindows;
import com.jason.liu.time.utils.TimeLogUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.core.env.Environment;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: meng.liu
 * @date: 2021/1/8
 * TODO:
 */
public class TimeConsumers {

    public static final String BEAN_NAME = "JasonToolsCustom$$timeConsumers";

    @Getter
    private TimeConsumeWindows timeConsumeWindows;

    @Getter
    private StatisticProperties statisticProperties;

    @Getter
    private int maxClassLen = Integer.MIN_VALUE;

    @Getter
    private int maxMethodLen = Integer.MIN_VALUE;

    private static final Pattern PATTERN = Pattern.compile("\\$\\{([a-zA-Z\\.0-9\\-]*)\\}");

    private Environment environment;

    private Map<Method, MethodInfo> methodInfoMap = new ConcurrentHashMap<>();

    private Map<String, MethodInfo> keyMethodInfoMap = new ConcurrentHashMap<>();

    public TimeConsumers(TimeConsumeWindows timeConsumeWindows,
                         StatisticProperties statisticProperties,
                         Environment environment) {
        this.timeConsumeWindows = timeConsumeWindows;
        this.statisticProperties = statisticProperties;
        this.environment = environment;
    }

    public void put(Method method, Class<?> clazz, TimeConsume timeConsume) {
        MethodInfo methodInfo = TimeLogUtils.buildMethodInfo(method, clazz, timeConsume);
        maxClassLen = Math.max(maxClassLen, methodInfo.getClassLen());
        maxMethodLen = Math.max(maxMethodLen, methodInfo.getMethodLen());
        methodInfoMap.put(method, methodInfo);
        keyMethodInfoMap.put(methodInfo.getKey(), methodInfo);
        Integer period = this.parseValue(timeConsume.period(), statisticProperties.getDefaultPeriod());
        Integer block = this.parseValue(timeConsume.block(), statisticProperties.getDefaultBlock());
        timeConsumeWindows.register(methodInfo.getKey(), period, block);
    }

    private Integer parseValue(String confStr, Integer defaultVal) {
        Integer value = null;
        Matcher matcher = PATTERN.matcher(confStr);
        if (matcher.matches()) {
            String perValStr = environment.getProperty(matcher.group(1));
            if (StringUtils.isNotBlank(perValStr)) {
                try {
                    value = Integer.parseInt(perValStr);
                } catch (Exception ignored) {
                }
            }
        } else {
            if (NumberUtils.isCreatable(confStr)) {
                value = NumberUtils.createInteger(confStr);
            }
        }
        return Optional.ofNullable(value).orElse(defaultVal);
    }

    public MethodInfo get(Method method) {
        return methodInfoMap.get(method);
    }

    public MethodInfo get(String key) {
        return keyMethodInfoMap.get(key);
    }
}
