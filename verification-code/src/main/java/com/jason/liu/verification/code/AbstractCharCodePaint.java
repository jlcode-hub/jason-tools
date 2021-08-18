package com.jason.liu.verification.code;

/**
 * @author: meng.liu
 * @date: 2021/4/7
 * TODO:
 */
public abstract class AbstractCharCodePaint implements ICodePaint {

    private Integer width;

    private Integer height;

    public AbstractCharCodePaint(Integer width, Integer height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 画布的宽度
     *
     * @param chars
     * @return
     */
    public int canvasWidth(String chars) {
        if (null != width) {
            return width;
        } else {
            return 20 * chars.length();
        }
    }

    /**
     * 画布的高度
     *
     * @param chars
     * @return
     */
    public int canvasHeight(String chars) {
        return 30;
    }

}
