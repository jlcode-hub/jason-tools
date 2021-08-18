package com.jason.liu.verification.code.pool.store;

import com.jason.liu.verification.code.constants.Const;
import com.jason.liu.verification.code.pool.ICodeStore;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO:
 */
public class RedisStore implements ICodeStore {

    private RedisTemplate<String, Object> redisTemplate;

    private long expire;

    public RedisStore(long expire, RedisTemplate<String, Object> redisTemplate) {
        this.expire = expire;
        this.redisTemplate = redisTemplate;
    }

    private ValueOperations<String, Object> valueOperations() {
        return redisTemplate.opsForValue();
    }

    @Override
    public void store(String requestId, String code) {
        this.valueOperations().set(this.cacheKey(requestId), code, this.expire, TimeUnit.SECONDS);
    }

    @Override
    public String getCode(String requestId) {
        return (String) this.valueOperations().get(this.cacheKey(requestId));
    }

    @Override
    public void remove(String requestId) {
        this.redisTemplate.delete(this.cacheKey(requestId));
    }

    private String cacheKey(String requestId) {
        return Const.VERIFICATION_ID + ":" + requestId;
    }
}
