package com.jason.liu.verification.code;

/**
 * @author: meng.liu
 * @date: 2021/4/7
 * TODO:
 */
public interface IValidator<Code> {

    /**
     * 校验
     *
     * @param code      存储器统一存储String
     * @param inputCode
     * @return
     */
    boolean valid(String code, Code inputCode);

}
