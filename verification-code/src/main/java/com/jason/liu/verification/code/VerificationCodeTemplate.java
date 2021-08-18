package com.jason.liu.verification.code;

import com.jason.liu.verification.code.constants.Const;
import com.jason.liu.verification.code.model.Base64VerificationCode;
import com.jason.liu.verification.code.model.Base64VerificationCodeVO;
import com.jason.liu.verification.code.model.ImageVerificationCode;
import com.jason.liu.verification.code.model.VerificationCodeDTO;
import com.jason.liu.verification.code.pool.CodeGeneratorImpl;
import com.jason.liu.verification.code.pool.ICodeStore;
import com.jason.liu.verification.code.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO:
 */
@Slf4j
public class VerificationCodeTemplate<Code> {

    private CodeGeneratorImpl codeGeneratorImpl;

    private ICodeStore codeStore;

    private IValidator<Code> validator;

    private IRequestIdExtractor requestIdExtractor;


    public VerificationCodeTemplate(CodeGeneratorImpl codeGeneratorImpl,
                                    ICodeStore codeStore,
                                    IValidator<Code> validator,
                                    IRequestIdExtractor requestIdExtractor) {
        this.codeGeneratorImpl = codeGeneratorImpl;
        this.codeStore = codeStore;
        this.validator = validator;
        this.requestIdExtractor = requestIdExtractor;
    }

    /**
     * 获取一个Base64编码的验证码
     *
     * @return
     */
    public Base64VerificationCodeVO getBase64() {
        String requestId = UUID.randomUUID().toString();
        Base64VerificationCode base64VerificationCode = this.codeGeneratorImpl.getBase64();
        if (log.isDebugEnabled()) {
            log.debug("get verification code for base64, code id: {}, code value: {}", requestId, base64VerificationCode.getCode());
        }
        this.codeStore.store(requestId, base64VerificationCode.getCode());
        Base64VerificationCodeVO base64VerificationCodeVO = new Base64VerificationCodeVO();
        base64VerificationCodeVO.setVcodeId(requestId);
        base64VerificationCodeVO.setImg64(base64VerificationCode.getBase64());
        return base64VerificationCodeVO;
    }


    /**
     * 获取一个图片验证码
     *
     * @return
     */
    public ImageVerificationCode getImage() {
        return this.codeGeneratorImpl.getImage();
    }

    /**
     * 直接输出一个验证码
     *
     * @param response
     */
    public void output(HttpServletResponse response) {
        String requestId = UUID.randomUUID().toString();
        CookieUtils.writeCookie(response, Const.VERIFICATION_ID, requestId);
        response.setDateHeader(HttpHeaders.EXPIRES, -1);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        ImageVerificationCode verificationCode = this.getImage();
        if (log.isDebugEnabled()) {
            log.debug("get verification code for base64, code id: {}, code value: {}", requestId, verificationCode.getCode());
        }
        this.codeStore.store(requestId, verificationCode.getCode());
        codeGeneratorImpl.getCodeGenerator().output(verificationCode, response);
    }

    /**
     * 校验
     *
     * @param request
     * @param inputCode
     * @return
     */
    public boolean valid(HttpServletRequest request, Code inputCode) {
        String requestId = requestIdExtractor.extract(request);
        if (StringUtils.isBlank(requestId)) {
            return false;
        }
        return this.valid(requestId, inputCode);
    }

    /**
     * 校验
     *
     * @param requestId
     * @param inputCode
     * @return
     */
    public boolean valid(String requestId, Code inputCode) {
        if (log.isDebugEnabled()) {
            log.debug("valid verification code, code id: {}, input value: {}", requestId, inputCode);
        }
        String code = this.codeStore.getCode(requestId);
        if (this.validator.valid(code, inputCode)) {
            this.codeStore.remove(requestId);
            return true;
        }
        return false;
    }

    /**
     * 校验
     *
     * @param verificationCode
     * @return
     */
    public boolean valid(VerificationCodeDTO<Code> verificationCode) {
        String code = this.codeStore.getCode(verificationCode.getVcodeId());
        if (this.validator.valid(code, verificationCode.getVerifyCode())) {
            this.codeStore.remove(verificationCode.getVcodeId());
            return true;
        }
        return false;
    }

}
