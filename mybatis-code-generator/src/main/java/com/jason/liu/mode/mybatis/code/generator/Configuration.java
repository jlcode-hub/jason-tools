package com.jason.liu.mode.mybatis.code.generator;

import org.springframework.util.ConcurrentReferenceHashMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * @author: LiuMeng
 * @date: 2019/11/13
 * TODO:
 */
public class Configuration {

    private static final Map<String, Properties> propertiesCache = new ConcurrentReferenceHashMap<>();

    private String propertiesFileName;

    private Properties properties;

    public Configuration(String propertiesFileName) {
        this.propertiesFileName = propertiesFileName;
        this.loadProperties();
    }

    private void loadProperties() {
        properties = propertiesCache.get(this.propertiesFileName);
        if (null == properties) {
            synchronized (propertiesCache) {
                properties = propertiesCache.get(this.propertiesFileName);
                if (null == properties) {
                    InputStream inputStream = MybatisPlusCodeGenerator.class.getClassLoader().getResourceAsStream(propertiesFileName);
                    try {
                        properties = new Properties();
                        properties.load(inputStream);
                        propertiesCache.put(this.propertiesFileName, properties);
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }

    }

    public String getString(String key) {
        return this.properties.getProperty(key);
    }

    public String getString(String key, String defaultValue) {
        return this.properties.getProperty(key, defaultValue);
    }

    public boolean getBoolean(String key) {
        String val = this.getString(key);
        return Boolean.valueOf(val);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String val = this.getString(key);
        if (null == val) {
            return defaultValue;
        }
        return Boolean.valueOf(val);
    }

    public Integer getInteger(String key) {
        String value = this.properties.getProperty(key);
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }

    public int getInteger(String key, int defaultValue) {
        String value = this.properties.getProperty(key);
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
