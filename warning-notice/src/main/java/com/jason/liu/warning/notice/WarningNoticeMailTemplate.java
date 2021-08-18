package com.jason.liu.warning.notice;

import com.jason.liu.warning.notice.model.TemplateMail;
import com.jason.liu.warning.notice.model.WarningNoticeTemplateMail;
import com.jason.liu.warning.notice.properties.WarningNoticeMailProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * 告警通知处理器
 *
 * @author meng.liu
 * @version 1.0
 * @date 2021-07-15 12:17:19
 */
@Slf4j
public class WarningNoticeMailTemplate implements InitializingBean {

    private final static String SUBJECT = "【Warn】业务异常告警通知";

    private MailTemplate mailTemplate;

    private final WarningNoticeMailProperty mailProperty;

    private final TemplateEngine templateEngine;

    private final IdempotentNotice idempotentNotice;

    private Executor executor;

    public WarningNoticeMailTemplate(WarningNoticeMailProperty mailProperty,
                                     IdempotentNotice idempotentNotice,
                                     TemplateEngine templateEngine) {
        this.mailProperty = mailProperty;
        this.idempotentNotice = idempotentNotice;
        this.templateEngine = templateEngine;
    }

    public void sendWarningNotice(String noticeId, WarningNoticeTemplateMail.WaningInfoBuilder waningInfoBuilder) {
        this.sendWarningNotice(noticeId, waningInfoBuilder.buildTemplate());
    }

    public void sendWarningNotice(String noticeId, String subject, WarningNoticeTemplateMail.WaningInfoBuilder waningInfoBuilder) {
        this.sendWarningNotice(noticeId, subject, waningInfoBuilder.buildTemplate());
    }

    public void sendWarningNotice(String noticeId, TemplateMail templateMail) {
        this.sendWarningNotice(noticeId, SUBJECT + "-" + this.mailProperty.getEnv(), templateMail);
    }

    public void sendWarningNotice(String noticeId, String subject, TemplateMail templateMail) {
        try {
            if (this.idempotentNotice.isSent(noticeId)) {
                log.debug("this is repeat notice for id {}", noticeId);
                return;
            }
            Map<String, Object> params = templateMail.getTemplateParams();
            if (null != params) {
                params.putIfAbsent("envName", this.mailProperty.getEnv());
            }
            this.executor.execute(() -> {
                try {
                    mailTemplate.sendTemplateMail(
                            this.mailProperty.getMailFrom(),
                            this.mailProperty.getReceivers().toArray(new String[0]),
                            subject,
                            templateMail);
                } catch (Exception e) {
                    log.error("sendWarningNotice exception", e);
                }
            });
        } catch (RejectedExecutionException e) {
            log.error("submit task to sendWarningNotice failed, the thread pool full.");
        }
    }

    public WarningNoticeMailTemplate setExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.mailProperty.getEnv(), "${warning.notice.mail.env} must be configured.");
        Assert.notEmpty(this.mailProperty.getReceivers(), "${warning.notice.mail.receivers} must be configured.");
        Assert.hasText(this.mailProperty.getUsername(), "${warning.notice.mail.username} must be configured");
        Assert.hasText(this.mailProperty.getPassword(), "${warning.notice.mail.password} must be configured");
        if (null == this.executor) {
            this.executor = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10240),
                    new NamedThreadFactory("WarningNoticeThread"));
        }
        if (null == mailTemplate) {
            JavaMailSender javaMailSender = this.mailSender();
            this.mailTemplate = new MailTemplate(javaMailSender, templateEngine);
        }
    }

    private JavaMailSender mailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(this.mailProperty.getHost());
        if (this.mailProperty.getPort() != null) {
            sender.setPort(this.mailProperty.getPort());
        }
        sender.setUsername(this.mailProperty.getUsername());
        sender.setPassword(this.mailProperty.getPassword());
        sender.setProtocol(this.mailProperty.getProtocol());
        if (this.mailProperty.getDefaultEncoding() != null) {
            sender.setDefaultEncoding(this.mailProperty.getDefaultEncoding().name());
        }
        if (!this.mailProperty.getProperties().isEmpty()) {
            Properties properties = new Properties();
            properties.putAll(this.mailProperty.getProperties());
            sender.setJavaMailProperties(properties);
        }
        return sender;
    }
}
