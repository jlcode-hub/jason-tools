package com.jason.liu.warning.notice;

import com.jason.liu.warning.notice.model.Attachment;
import com.jason.liu.warning.notice.model.TemplateMail;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

/**
 * 告警模板处理器
 *
 * @author meng.liu
 * @version 1.0
 * @date 2021-07-15 09:50:12
 */
public class MailTemplate {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    public MailTemplate(JavaMailSender mailSender,
                        TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * 发送普通邮件
     *
     * @param mailFrom
     * @param mailTo
     * @param subject
     * @param content
     */
    public void sendSimpleMail(String mailFrom, String mailTo, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(mailTo);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    /**
     * 发送模板邮件
     *
     * @param mailFrom
     * @param mailTo
     * @param subject
     * @param templateMail
     */
    public void sendTemplateMail(String mailFrom, String mailTo, String subject, TemplateMail templateMail) {
        sendTemplateMail(mailFrom, mailTo, subject, templateMail, null);
    }

    /**
     * 发送模板邮件
     *
     * @param mailFrom
     * @param mailTos
     * @param subject
     * @param templateMail
     */
    public void sendTemplateMail(String mailFrom, String[] mailTos, String subject, TemplateMail templateMail) {
        sendTemplateMail(mailFrom, mailTos, subject, templateMail, null);
    }

    /**
     * 发送模板邮件
     *
     * @param mailFrom
     * @param mailTo
     * @param subject
     * @param templateMail
     * @param attachments
     */
    public void sendTemplateMail(String mailFrom, String mailTo, String subject, TemplateMail templateMail, List<Attachment> attachments) {
        Context context = new Context();
        context.setVariables(templateMail.getTemplateParams());
        //todo 解析模板
        String emailContent = templateEngine.process(templateMail.getTemplateName(), context);
        sendAttachmentsMail(mailFrom, mailTo, subject, emailContent, attachments);
    }


    /**
     * 发送模板邮件
     *
     * @param mailFrom
     * @param mailTos
     * @param subject
     * @param templateMail
     * @param attachments
     */
    public void sendTemplateMail(String mailFrom, String[] mailTos, String subject, TemplateMail templateMail, List<Attachment> attachments) {
        Context context = new Context();
        context.setVariables(templateMail.getTemplateParams());
        //todo 解析模板
        String emailContent = templateEngine.process(templateMail.getTemplateName(), context);
        sendAttachmentsMail(mailFrom, mailTos, subject, emailContent, attachments);
    }


    /**
     * 发送带附件的邮件
     *
     * @param mailFrom
     * @param mailTo
     * @param subject
     * @param content
     * @param attachments
     */
    public void sendAttachmentsMail(String mailFrom, String mailTo, String subject, String content, List<Attachment> attachments) {
        this.sendAttachmentsMail(mailFrom, helper -> helper.setTo(mailTo), subject, content, attachments);
    }

    /**
     * 发送带附件的邮件
     *
     * @param mailFrom
     * @param mailTos
     * @param subject
     * @param content
     * @param attachments
     */
    public void sendAttachmentsMail(String mailFrom, String[] mailTos, String subject, String content, List<Attachment> attachments) {
        this.sendAttachmentsMail(mailFrom, helper -> helper.setTo(mailTos), subject, content, attachments);
    }

    /**
     * 发送带附件的邮件
     *
     * @param mailFrom
     * @param receiver
     * @param subject
     * @param content
     * @param attachments
     */
    public void sendAttachmentsMail(String mailFrom, Receiver receiver, String subject, String content, List<Attachment> attachments) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailFrom);
            receiver.accept(helper);
            helper.setSubject(subject);
            helper.setText(content, true);
            if (!CollectionUtils.isEmpty(attachments)) {
                for (Attachment attachment : attachments) {
                    FileSystemResource file = new FileSystemResource(attachment.getFile());
                    helper.addAttachment(attachment.getName(), file);
                }
            }
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    interface Receiver {

        void accept(MimeMessageHelper helper) throws MessagingException;

    }
}
