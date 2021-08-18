package com.jason.liu.nacos.refresh.utils;

import org.springframework.core.env.Environment;

/**
 * @author: meng.liu
 * @date: 2021/2/18
 * TODO:
 */
public class SystemProperty {

    private static Environment environment;

    public static void setEnvironment(Environment environment) {
        SystemProperty.environment = environment;
    }

    public static Integer getInteger(String s, Integer defVal) {
        Integer val = getInteger(s);
        return null == val ? defVal : val;
    }

    public static Integer getInteger(String s) {
        try {
            return Integer.valueOf(getString(s));
        } catch (Exception e) {
            return null;
        }
    }

    public static Long getLong(String s, Long defVal) {
        Long val = getLong(s);
        return null == val ? defVal : val;
    }

    public static Long getLong(String s) {
        try {
            return Long.valueOf(getString(s));
        } catch (Exception e) {
            return null;
        }
    }

    public static Double getDouble(String s, Double defVal) {
        Double val = getDouble(s);
        return null == val ? defVal : val;
    }

    public static Double getDouble(String s) {
        try {
            return Double.valueOf(getString(s));
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean getBoolean(String s, Boolean defVal) {
        Boolean val = getBoolean(s);
        return null == val ? defVal : val;
    }

    public static Boolean getBoolean(String s) {
        try {
            return Boolean.valueOf(getString(s));
        } catch (Exception e) {
            return null;
        }
    }

    public static String getString(String s, String defVal) {
        return environment.getProperty(s, defVal);
    }

    public static String getString(String s) {
        return environment.getProperty(s);
    }
}
