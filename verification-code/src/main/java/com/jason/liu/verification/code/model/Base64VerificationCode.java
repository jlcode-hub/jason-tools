package com.jason.liu.verification.code.model;

import lombok.Data;

import java.awt.image.BufferedImage;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO:
 */
@Data
public class Base64VerificationCode extends VerificationCode {

    /**
     * base64位值
     */
    private String base64;

    public ImageVerificationCode toImage(BufferedImage image) {
        ImageVerificationCode verificationCode = new ImageVerificationCode();
        verificationCode.setCode(this.getCode());
        verificationCode.setImageType(this.getImageType());
        verificationCode.setImage(image);
        return verificationCode;
    }
}
