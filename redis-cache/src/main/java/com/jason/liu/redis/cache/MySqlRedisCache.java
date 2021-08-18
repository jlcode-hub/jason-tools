package com.jason.liu.redis.cache;

import com.jason.liu.redis.CacheContext;
import com.jason.liu.redis.RedisTemplateSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.Cache;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-05-26 17:54:00
 * @todo
 */
@Slf4j
public class MySqlRedisCache implements Cache {

    private static final Pattern LONG_PATTERN = Pattern.compile("^java\\.lang\\.Long\\((\\d+)\\)$");

    private String prefix = "MySqlCache:";

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final String id;

    private final String clearKey;

    private Long expire;

    private RedisTemplateSupport redisTemplate;

    public RedisTemplateSupport getRedisTemplate() {
        if (null == redisTemplate) {
            redisTemplate = CacheContext.getRedisTemplate();
        }
        return redisTemplate;
    }

    private long getExpireTimeOut() {
        if (null == this.expire) {
            this.expire = CacheContext.getMysqlRedisCacheProperty().getExpire();
        }
        return randomExpire(this.expire);
    }

    public MySqlRedisCache(String id) {
        if (null == id) {
            throw new IllegalArgumentException("error, mysql redis cache instances require an ID");
        }
        log.info("init mysql redis cache for : {}", id);
        this.id = id;
        this.clearKey = this.prefix + "*" + this.id + "*";
    }

    @Override
    public String getId() {
        return this.id;
    }

    private String getKey(Object key) {
        return this.prefix + key.toString();
    }

    @Override
    public void putObject(Object key, Object value) {
        Object cacheValue;
        if (value instanceof Long) {
            cacheValue = "java.lang.Long(" + value + ")";
        } else {
            cacheValue = value;
        }
        this.getRedisTemplate().addValue(getKey(key), cacheValue, getExpireTimeOut(), TimeUnit.SECONDS);
    }

    @Override
    public Object getObject(Object key) {
        try {
            if (null != key) {
                Object cacheObj = this.getRedisTemplate().getValue(getKey(key));
                if (null != cacheObj) {
                    if (cacheObj instanceof String && StringUtils.isNotBlank((String) cacheObj)) {
                        Matcher matcher = LONG_PATTERN.matcher((String) cacheObj);
                        if (matcher.find()) {
                            return Long.valueOf(matcher.group(1));
                        }
                    }
                }
                return cacheObj;
            }
        } catch (Exception e) {
            log.warn("", e);
        }
        return null;
    }

    @Override
    public Object removeObject(Object key) {
        try {
            if (null != key) {
                this.getRedisTemplate().delete(getKey(key));
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.error("remove mysql redis key exception : {}", key, e);
            }
        }
        return null;
    }

    @Override
    public void clear() {
        try {
            Set<String> keys = this.getRedisTemplate().keys(this.clearKey);
            if (!CollectionUtils.isEmpty(keys)) {
                this.getRedisTemplate().delete(keys);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.error("clear mysql redis key exception : {}", clearKey, e);
            }
        }
    }

    @Override
    public int getSize() {
        return this.getRedisTemplate().keys(this.clearKey).size();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return this.readWriteLock;
    }


    private static final long RandomRange = 30L;


    /**
     * 在1分钟的范围内浮动缓存过期时间，防止大量缓存瞬时过期导致数据库瞬时高并发
     *
     * @param avgTime
     * @return
     */
    public static long randomExpire(long avgTime) {
        long min = avgTime - RandomRange;
        return Math.round(Math.random() * RandomRange * 2 + min);
    }

}
