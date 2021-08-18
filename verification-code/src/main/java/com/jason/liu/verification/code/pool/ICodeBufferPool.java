package com.jason.liu.verification.code.pool;

import com.jason.liu.verification.code.model.Base64VerificationCode;

import java.util.List;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO: Code存储
 */
public interface ICodeBufferPool {

    /**
     * 获取当前存储的数量
     *
     * @return
     */
    long size();

    /**
     * 获取一个验证码
     *
     * @return
     */
    Base64VerificationCode get();

    /**
     * 存储一个
     *
     * @param codes
     */
    void store(List<Base64VerificationCode> codes);

    /**
     * 设置pool Id
     *
     * @param poolId
     */
    void setPoolId(String poolId);
}
