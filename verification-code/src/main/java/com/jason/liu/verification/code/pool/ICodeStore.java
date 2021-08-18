package com.jason.liu.verification.code.pool;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO: 验证码存储器
 */
public interface ICodeStore {

    /**
     * 存储请求ID和验证码值
     *
     * @param requestId
     * @param code
     */
    void store(String requestId, String code);

    /**
     * 根据请求ID查询验证码
     *
     * @param requestId
     * @return
     */
    String getCode(String requestId);

    /**
     * 移除Key
     *
     * @param requestId
     */
    void remove(String requestId);
}
