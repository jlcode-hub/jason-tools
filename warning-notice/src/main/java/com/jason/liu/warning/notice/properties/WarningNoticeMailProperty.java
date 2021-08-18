package com.jason.liu.warning.notice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 告警通知邮箱配置
 *
 * @author meng.liu
 * @version 1.0
 * @date 2021-07-15 12:59:59
 */
@Data
@ConfigurationProperties("jason.tools.notice.mail")
public class WarningNoticeMailProperty extends MainProperty {
    /**
     * 环境
     */
    private String env;

    /**
     * 邮件发送来源
     */
    private String mailFrom = "<业务监控系统>";

    /**
     * 接收方
     */
    private List<String> receivers;
}
