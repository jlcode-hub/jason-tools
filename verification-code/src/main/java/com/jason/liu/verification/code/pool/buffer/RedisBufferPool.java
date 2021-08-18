package com.jason.liu.verification.code.pool.buffer;

import com.jason.liu.verification.code.model.Base64VerificationCode;
import com.jason.liu.verification.code.pool.ICodeBufferPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.List;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO:
 */
public class RedisBufferPool implements ICodeBufferPool {

    private static final String DEFAULT_QUEUE_NAME = "JasonToolsCustom$$verificationStoreQueue:";

    private String queueName;

    @Value("${spring.application.name:UnnamedApplication}")
    private String applicationName;

    private RedisTemplate<String, Object> redisTemplate;

    public RedisBufferPool(RedisTemplate<String, Object> redisTemplate, String queueName) {
        this.redisTemplate = redisTemplate;
        this.queueName = queueName;
    }

    private ListOperations<String, Object> listOperations() {
        return this.redisTemplate.opsForList();
    }

    @Override
    public long size() {
        Long length = listOperations().size(this.queueName());
        return null == length ? 0 : length;
    }

    @Override
    public Base64VerificationCode get() {
        return (Base64VerificationCode) listOperations().rightPop(this.queueName());
    }

    @Override
    public void store(List<Base64VerificationCode> codes) {
        listOperations().leftPushAll(this.queueName(), (Collection) codes);
    }

    private String queueName() {
        return DEFAULT_QUEUE_NAME + applicationName + ":" + this.queueName;
    }

    @Override
    public void setPoolId(String poolId) {
        this.queueName = poolId;
    }
}
