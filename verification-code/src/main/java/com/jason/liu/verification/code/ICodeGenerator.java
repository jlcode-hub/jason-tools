package com.jason.liu.verification.code;

import com.jason.liu.verification.code.model.ImageVerificationCode;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO:
 */
public interface ICodeGenerator extends Base64Converter, OutputImage {

    /**
     * 创建一个验证码
     *
     * @return
     */
    ImageVerificationCode createCode();

    /**
     * 生成器名称
     *
     * @return
     */
    String generatorName();
}
