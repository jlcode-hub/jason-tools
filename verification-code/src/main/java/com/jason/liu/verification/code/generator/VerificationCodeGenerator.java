package com.jason.liu.verification.code.generator;

import com.jason.liu.verification.code.ICodeGenerator;
import com.jason.liu.verification.code.ICodePaint;
import com.jason.liu.verification.code.ICodeRule;
import com.jason.liu.verification.code.model.Base64VerificationCode;
import com.jason.liu.verification.code.model.ImageVerificationCode;
import com.jason.liu.verification.code.model.RandomCode;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO: 验证码生成器
 */
@Slf4j
public class VerificationCodeGenerator implements ICodeGenerator {
    /**
     * code规则
     */
    private ICodeRule codeRule;
    /**
     * 绘制画笔
     */
    private ICodePaint codePaint;

    public VerificationCodeGenerator(ICodeRule codeRule, ICodePaint codePaint) {
        this.codeRule = codeRule;
        this.codePaint = codePaint;
    }

    @Override
    public ImageVerificationCode createCode() {
        RandomCode randomCode = codeRule.createCode();
        ImageVerificationCode verificationCode = new ImageVerificationCode();
        verificationCode.setCode(randomCode.getVerifyCode());
        verificationCode.setImage(codePaint.paintImage(randomCode.getShowCode()));
        verificationCode.setImageType(this.codePaint.imageType());
        log.debug("generate new verification code: {}", randomCode.getVerifyCode());
        return verificationCode;
    }

    @Override
    public Base64VerificationCode toBase64(ImageVerificationCode verificationCode) {
        return verificationCode.toBase64(this.codePaint.toBase64(verificationCode.getImage()));
    }

    @Override
    public ImageVerificationCode toImage(Base64VerificationCode verificationCode) {
        return verificationCode.toImage(this.codePaint.toImage(verificationCode.getBase64()));
    }

    @Override
    public void output(ImageVerificationCode verificationCode, HttpServletResponse response) {
        try {
            response.setContentType(this.codePaint.imageType().getMimeType());
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(verificationCode.getImage(), this.codePaint.imageType().getFormatName(), outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("write to out put exception", e);
        }
    }

    @Override
    public String generatorName() {
        return this.codeRule.ruleType() + "$" + this.codePaint.imageType();
    }
}
