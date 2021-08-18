package com.jason.liu.verification.code;

import com.jason.liu.verification.code.model.Base64VerificationCode;
import com.jason.liu.verification.code.model.ImageVerificationCode;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO:
 */
public interface Base64Converter {

    /**
     * 转为Base64编码
     *
     * @param verificationCode
     * @return
     */
    Base64VerificationCode toBase64(ImageVerificationCode verificationCode);

    /**
     * base64位转图片
     *
     * @param verificationCode
     * @return
     */
    ImageVerificationCode toImage(Base64VerificationCode verificationCode);

}
