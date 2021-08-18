package com.jason.liu.verification.code;

import com.jason.liu.verification.code.model.ImageVerificationCode;

import javax.servlet.http.HttpServletResponse;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO:
 */
public interface OutputImage {

    /**
     * 输出图片
     *
     * @param verificationCode
     * @param response
     */
    void output(ImageVerificationCode verificationCode, HttpServletResponse response);

}
