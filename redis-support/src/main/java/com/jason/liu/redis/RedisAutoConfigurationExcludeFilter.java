package com.jason.liu.redis;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;

/**
 * Redis自动装配排除
 *
 * @author meng.liu
 * @version 1.0
 * @date 2021-07-20 18:48:26
 */
public class RedisAutoConfigurationExcludeFilter implements AutoConfigurationImportFilter {

    private static final String REDIS_AUTO = "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration";

    @Override
    public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        boolean[] result = new boolean[autoConfigurationClasses.length];
        for (int i = 0; i < autoConfigurationClasses.length; i++) {
            result[i] = !REDIS_AUTO.equals(autoConfigurationClasses[i]);
        }
        return result;
    }
}
