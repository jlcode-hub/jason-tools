package com.jason.liu.verification.code;

import com.jason.liu.verification.code.constants.CodeRuleType;
import com.jason.liu.verification.code.model.RandomCode;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO:
 */
public interface ICodeRule {

    /**
     * 创建一个验证码
     *
     * @return
     */
    RandomCode createCode();

    /**
     * 生成的图片类型
     *
     * @return
     */
    CodeRuleType ruleType();
}
