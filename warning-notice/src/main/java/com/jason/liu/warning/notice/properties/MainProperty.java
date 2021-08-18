package com.jason.liu.warning.notice.properties;

import lombok.Data;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 邮箱配置
 *
 * @author meng.liu
 * @version 1.0
 * @date 2021-07-15 13:01:37
 */
@Data
public class MainProperty {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private static final String DEFAULT_HOST = "smtp.exmail.qq.com";

    /**
     * 主机地址
     */
    private String host = DEFAULT_HOST;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 协议
     */
    private String protocol = "smtp";
    /**
     * 编码方式
     */
    private Charset defaultEncoding = DEFAULT_CHARSET;

    /**
     * 其他配置
     */
    private Map<String, String> properties = new HashMap<>();
}
