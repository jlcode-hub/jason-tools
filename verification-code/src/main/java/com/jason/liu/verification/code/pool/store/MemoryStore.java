package com.jason.liu.verification.code.pool.store;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jason.liu.verification.code.pool.ICodeStore;

import java.util.concurrent.TimeUnit;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO:
 */
public class MemoryStore implements ICodeStore {

    private Cache<String, String> caffeine;

    public MemoryStore(long expire) {
        this.caffeine = Caffeine.newBuilder()
                .expireAfterWrite(expire, TimeUnit.SECONDS)
                .maximumSize(10000)
                .build();
    }

    @Override
    public void store(String requestId, String code) {
        caffeine.put(requestId, code);
    }

    @Override
    public String getCode(String requestId) {
        return caffeine.getIfPresent(requestId);
    }

    @Override
    public void remove(String requestId) {
        caffeine.invalidate(requestId);
    }
}
