package com.jason.liu.verification.code.pool.buffer;

import com.jason.liu.verification.code.model.Base64VerificationCode;
import com.jason.liu.verification.code.pool.ICodeBufferPool;

import java.util.LinkedList;
import java.util.List;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO:
 */
public class MemoryBufferPool implements ICodeBufferPool {

    private String poolId;

    private final LinkedList<Base64VerificationCode> cache = new LinkedList<>();

    @Override
    public long size() {
        return cache.size();
    }

    @Override
    public Base64VerificationCode get() {
        return cache.poll();
    }

    @Override
    public void store(List<Base64VerificationCode> codes) {
        codes.forEach(cache::offer);
    }

    @Override
    public void setPoolId(String poolId) {
        this.poolId = poolId;
    }
}
