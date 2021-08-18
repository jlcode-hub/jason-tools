package com.jason.liu.verification.code.constants;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO: 验证码类型
 */
@Getter
public enum CodeRuleType implements Serializable {
    /**
     * 随机字符
     */
    RANDOM_CHAR,
    /**
     * 四则运算
     */
    ARITHMETIC,
    ;
}
