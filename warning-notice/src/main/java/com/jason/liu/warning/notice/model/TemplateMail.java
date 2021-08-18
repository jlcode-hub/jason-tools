package com.jason.liu.warning.notice.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 模板邮件信息
 *
 * @author meng.liu
 * @version 1.0
 * @date 2021-07-15 10:03:36
 */
@Data
public class TemplateMail implements Serializable {

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板参数
     */
    private Map<String, Object> templateParams;


    public static TemplateMail of(String templateName, Map<String, Object> templateParams) {
        TemplateMail templateMail = new TemplateMail();
        templateMail.setTemplateName(templateName);
        templateMail.setTemplateParams(templateParams);
        return templateMail;
    }
}
