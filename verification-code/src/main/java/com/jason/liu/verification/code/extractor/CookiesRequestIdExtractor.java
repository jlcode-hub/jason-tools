package com.jason.liu.verification.code.extractor;

import com.jason.liu.verification.code.IRequestIdExtractor;
import com.jason.liu.verification.code.constants.Const;
import com.jason.liu.verification.code.utils.CookieUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: meng.liu
 * @date: 2021/4/7
 * TODO:
 */
public class CookiesRequestIdExtractor implements IRequestIdExtractor {

    @Override
    public String extract(HttpServletRequest request) {
        return CookieUtils.getCookie(request, Const.VERIFICATION_ID);
    }
}
