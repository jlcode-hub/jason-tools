package com.jason.liu.verification.code.utils;

import java.awt.*;
import java.util.Random;

/**
 * @author: meng.liu
 * @date: 2021/4/6
 * TODO:
 */
public class RandomPaint {

    private static final Random RANDOM = new Random();

    private static final String[] FONTS = {"Time News Roman", "宋体", "微软雅黑"};

    /**
     * 在指定范围内的随机颜色
     *
     * @param min
     * @param max
     * @return
     */
    public static Color randomColor(int min, int max) {
        if (min > 255) {
            min = 255;
        }
        if (max > 255) {
            max = 255;
        }
        int r = min + RANDOM.nextInt(max - min);
        int g = min + RANDOM.nextInt(max - min);
        int b = min + RANDOM.nextInt(max - min);
        return new Color(r, g, b);
    }

    public static Color randomColor() {
        return randomColor(0, 255);
    }

    /**
     * 随机字体
     *
     * @return
     */
    public static Font randomFont() {
        int fontIndex = RANDOM.nextInt(FONTS.length);
        int fontSize = 17 + RANDOM.nextInt(3);
        return new Font(FONTS[fontIndex], Font.PLAIN, fontSize);
    }

}
