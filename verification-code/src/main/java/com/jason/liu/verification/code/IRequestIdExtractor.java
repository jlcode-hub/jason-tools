package com.jason.liu.verification.code;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: meng.liu
 * @date: 2021/4/7
 * TODO: 请求标识提取器
 */
public interface IRequestIdExtractor {

    /**
     * 请求参数提取器
     *
     * @param request
     * @return requetId
     */
    String extract(HttpServletRequest request);

}
