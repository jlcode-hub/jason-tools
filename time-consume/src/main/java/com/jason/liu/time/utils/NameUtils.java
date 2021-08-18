package com.jason.liu.time.utils;

import com.jason.liu.time.consume.TimeConsume;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * @author: meng.liu
 * @date: 2021/1/8
 * TODO:
 */
public class NameUtils {

    public static String className(Class<?> clazz) {
        return nameFormat(clazz.getSimpleName(), 30);
    }

    public static String methodName(Method method) {
        return nameFormat(method.getName(), 30);
    }

    public static String key(TimeConsume timeConsume, String className, String methodName) {
        String key = timeConsume.key();
        if (StringUtils.isBlank(key)) {
            key = className + "#" + methodName;
        }
        return key;
    }

    public static String nameFormat(String name, int maxLen) {
        return nameFormat(name, maxLen, "*");
    }

    public static String nameFormat(String name, int maxLen, String placeholder) {
        if (name.length() <= maxLen || maxLen < 5) {
            return name;
        }
        char[] chars = name.toCharArray();
        int cLen = (maxLen - 3) / 2;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cLen; i++) {
            builder.append(chars[i]);
        }
        builder.append(placeholder).append(placeholder).append(placeholder);
        for (int i = chars.length - cLen; i < chars.length; i++) {
            builder.append(chars[i]);
        }
        return builder.toString();
    }
}
