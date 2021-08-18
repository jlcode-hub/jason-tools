package com.jason.liu.warning.notice.model;

import lombok.Data;

import java.io.File;
import java.io.Serializable;

/**
 * 邮件的附件
 *
 * @author meng.liu
 * @version 1.0
 * @date 2021-07-15 09:56:46
 */
@Data
public class Attachment implements Serializable {
    /**
     * 附件名称
     */
    private String name;
    /**
     * 附件
     */
    private File file;

    public static Attachment of(String name, File file) {
        Attachment attachment = new Attachment();
        attachment.setName(name);
        attachment.setFile(file);
        return attachment;
    }
}
