package com.jason.liu.verification.code.validator;

import com.jason.liu.verification.code.IValidator;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: meng.liu
 * @date: 2021/4/7
 * TODO: 忽略大小写的字符校验器
 */
public class IgnoreCaseCharEqualsValidator implements IValidator<String> {

    @Override
    public boolean valid(String code, String inputCode) {
        if (StringUtils.isBlank(inputCode)) {
            throw new IllegalArgumentException("'input code is empty.");
        }
        return inputCode.equalsIgnoreCase(code);
    }
}
