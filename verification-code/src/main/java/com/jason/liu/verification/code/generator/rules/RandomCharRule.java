package com.jason.liu.verification.code.generator.rules;

import com.jason.liu.verification.code.ICodeRule;
import com.jason.liu.verification.code.constants.CodeRuleType;
import com.jason.liu.verification.code.model.RandomCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: meng.liu
 * @date: 2021/4/6
 * TODO: 随机字符生成器
 */
@Slf4j
public class RandomCharRule implements ICodeRule {

    private static final char[] CHARS = "1234567890ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();

    private int charLen;

    public RandomCharRule(Integer charLen) {
        if (null == charLen || charLen > 10) {
            log.warn("char length is null or more than 10, change to 6");
            charLen = 6;
        } else {
            this.charLen = charLen;
        }
    }

    @Override
    public RandomCode createCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < charLen; i++) {
            sb.append(CHARS[(int) (CHARS.length * Math.random())]);
        }
        String code = sb.toString();
        return new RandomCode(code, code);
    }

    @Override
    public CodeRuleType ruleType() {
        return CodeRuleType.RANDOM_CHAR;
    }
}
