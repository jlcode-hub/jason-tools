package com.jason.liu.verification.code;

import com.jason.liu.verification.code.constants.ImageType;

import java.awt.image.BufferedImage;

/**
 * @author: meng.liu
 * @date: 2021/4/6
 * TODO:
 */
public interface ICodePaint extends Base64ImageConverter {

    /**
     * 绘制图片
     *
     * @param code
     * @param width
     * @param height
     * @return
     */
    BufferedImage paintImage(String code);

    /**
     * 绘制的图片格式
     *
     * @return
     */
    ImageType imageType();
}
