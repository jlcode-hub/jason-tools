package com.jason.liu.verification.code.generator.paint;

import com.jason.liu.verification.code.AbstractCharCodePaint;
import com.jason.liu.verification.code.constants.ImageType;
import com.jason.liu.verification.code.properties.CodeProperty;
import com.jason.liu.verification.code.utils.Base64Utils;
import com.jason.liu.verification.code.utils.RandomPaint;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * @author: meng.liu
 * @date: 2021/4/6
 * TODO: JPEG图片绘制器
 */
public class JpegCodePaint extends AbstractCharCodePaint {

    private static final Random RANDOM = new Random();

    /**
     * 背景色
     */
    private static int COLOR_MIN_BG = 230;
    /***
     * 背景色
     */
    private static int COLOR_MAX_BG = 250;

    /**
     * 干扰线
     */
    private static int COLOR_MIN_LINE = 160;
    /**
     * 干扰线
     */
    private static int COLOR_MAX_LINE = 200;

    /**
     * 验证码字体颜色
     */
    private static int COLOR_MIN_CODE = 20;
    /***
     * 验证码字体颜色
     */
    private static int COLOR_MAX_CODE = 170;

    public JpegCodePaint(CodeProperty.ImageSize imageSize) {
        super(imageSize.getWidth(), imageSize.getHeight());
    }

    @Override
    public BufferedImage paintImage(String randomStr) {
        int width = this.canvasWidth(randomStr),
                height = this.canvasHeight(randomStr);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        //todo 背景填充白色
        Color bgColor = RandomPaint.randomColor(COLOR_MIN_BG, COLOR_MAX_BG);
        g.setColor(bgColor);
        g.fillRect(0, 0, width, height);
        //todo 添加干扰线
        int charLen = randomStr.length();
        for (int i = 0; i < charLen * 15; i++) {
            //todo 随机颜色
            g.setColor(RandomPaint.randomColor(COLOR_MIN_LINE, COLOR_MAX_LINE));
            int x = RANDOM.nextInt(width);
            int y = RANDOM.nextInt(height);
            int xl = RANDOM.nextInt(12);
            int yl = RANDOM.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }
        //todo 添加噪点
        float yawpRate = 0.02f;
        int area = (int) (yawpRate * width * height);
        for (int i = 0; i < area; i++) {
            int x = RANDOM.nextInt(width);
            int y = RANDOM.nextInt(height);
            Color rgb = RandomPaint.randomColor();
            image.setRGB(x, y, rgb.getRGB());
        }
        int x = 0;
        for (char c : randomStr.toCharArray()) {
            //todo 随机字体
            g.setFont(RandomPaint.randomFont());
            //todo 随机颜色
            g.setColor(RandomPaint.randomColor(COLOR_MIN_CODE, COLOR_MAX_CODE));
            g.drawString(c + "", 5 + x, 20);
            x += RANDOM.nextInt(6) + 15;
        }
        g.dispose();
        return image;
    }

    @Override
    public String toBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream bot = new ByteArrayOutputStream();
            ImageIO.write(image, this.imageType().getFormatName(), bot);
            String v64 = Base64Utils.encodeToString(bot.toByteArray()).replace("\n", "");
            bot.close();
            return v64;
        } catch (IOException e) {
            throw new RuntimeException("convert image to base64 failed.", e);
        }
    }

    @Override
    public BufferedImage toImage(String base64) {
        try {
            byte[] bytes = Base64Utils.decodeFromString(base64);
            ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
            BufferedImage image = ImageIO.read(bin);
            bin.close();
            return image;
        } catch (IOException e) {
            throw new RuntimeException("convert base64 to image failed", e);
        }
    }

    @Override
    public ImageType imageType() {
        return ImageType.JPEG;
    }
}
