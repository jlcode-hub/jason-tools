package com.jason.liu.verification.code.model;

import com.jason.liu.verification.code.constants.ImageType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO:
 */
@Data
public class VerificationCode implements Serializable {

    /**
     * 验证码
     */
    private String code;

    /**
     * 验证码类型
     */
    private ImageType imageType;
}
