package com.jason.liu.verification.code.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: meng.liu
 * @date: 2021/4/6
 * TODO:
 */
@Data
public class RandomCode implements Serializable {
    /**
     * 展示码
     */
    private String showCode;
    /**
     * 校验结果
     */
    private String verifyCode;

    public RandomCode(String verifyCode, String showCode) {
        this.verifyCode = verifyCode;
        this.showCode = showCode;
    }
}
