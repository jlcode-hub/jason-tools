package com.jason.liu.redis.support;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-05-27 09:17:49
 * @todo
 */
public class KeyPrefixHolder {

    private static final Map<RedisTemplate, ThreadLocal<Boolean>> PREFIX_HOLDER = new ConcurrentHashMap<>();

    public static <RT extends RedisTemplate> RT withoutPrefix(RT template) {
        assert null != template;
        prefixFlag(template).set(false);
        return template;
    }

    private static ThreadLocal<Boolean> prefixFlag(RedisTemplate template) {
        assert null != template;
        ThreadLocal<Boolean> threadLocal = PREFIX_HOLDER.get(template);
        if (null == threadLocal) {
            synchronized (PREFIX_HOLDER) {
                threadLocal = PREFIX_HOLDER.get(template);
                if (null == threadLocal) {
                    threadLocal = new ThreadLocal<>();
                    PREFIX_HOLDER.put(template, threadLocal);
                }
            }
        }
        return threadLocal;
    }

    static <RT extends RedisTemplate> boolean isWithPrefix(RT template) {
        Boolean withPrefix = prefixFlag(template).get();
        if (null == withPrefix || withPrefix) {
            return true;
        }
        return false;
    }

    public static <RT extends RedisTemplate> void clear(RT template) {
        PREFIX_HOLDER.remove(template);
    }
}
