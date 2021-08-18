package com.jason.liu.warning.notice;

import com.jason.liu.warning.notice.properties.WarningNoticeMailProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.TemplateEngine;

/**
 * 告警通知装配
 *
 * @author meng.liu
 * @version 1.0
 * @date 2021-07-15 09:45:15
 */
@EnableConfigurationProperties({WarningNoticeMailProperty.class})
public class WarningNoticeMailAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public IdempotentNotice idempotentNotice() {
        return noticeId -> false;
    }

    @Bean
    public WarningNoticeMailTemplate warningNoticeTemplate(
            WarningNoticeMailProperty mailProperty,
            IdempotentNotice idempotentNotice,
            TemplateEngine templateEngine) {
        return new WarningNoticeMailTemplate(mailProperty, idempotentNotice, templateEngine);
    }
}
