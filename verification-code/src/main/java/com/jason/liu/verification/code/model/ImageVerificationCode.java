package com.jason.liu.verification.code.model;

import lombok.Data;

import java.awt.image.BufferedImage;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO:
 */
@Data
public class ImageVerificationCode extends VerificationCode {

    /**
     * 图片
     */
    private BufferedImage image;

    public Base64VerificationCode toBase64(String base64) {
        Base64VerificationCode verificationCode = new Base64VerificationCode();
        verificationCode.setCode(this.getCode());
        verificationCode.setImageType(this.getImageType());
        verificationCode.setBase64(base64);
        return verificationCode;
    }
}
