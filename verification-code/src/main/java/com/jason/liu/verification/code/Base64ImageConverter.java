package com.jason.liu.verification.code;

import java.awt.image.BufferedImage;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO:
 */
public interface Base64ImageConverter {

    /**
     * 转为Base64编码
     *
     * @param bufferedImage
     * @return
     */
    String toBase64(BufferedImage bufferedImage);

    /**
     * base64位转图片
     *
     * @param base64
     * @return
     */
    BufferedImage toImage(String base64);

}
