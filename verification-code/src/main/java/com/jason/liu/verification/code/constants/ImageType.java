package com.jason.liu.verification.code.constants;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO: 验证码类型
 */
@Getter
public enum ImageType implements Serializable {
    /**
     * 静态图片
     */
    JPEG("jpg", "image/jpeg"),
    ;

    /**
     * 文件后缀
     */
    private String formatName;

    /**
     * 文件格式
     */
    private String mimeType;

    ImageType(String formatName, String mimeType) {
        this.mimeType = mimeType;
        this.formatName = formatName;
    }
}
