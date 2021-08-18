package com.jason.liu.warning.notice;

/**
 * 告警通知异常
 *
 * @author meng.liu
 * @version 1.0
 * @date 2021-07-20 09:43:51
 */
public class WarningNoticeException extends RuntimeException {

    public WarningNoticeException() {

    }

    public WarningNoticeException(String message) {
        super(message);
    }

    public WarningNoticeException(String message, Throwable cause) {
        super(message, cause);
    }

    public WarningNoticeException(Throwable cause) {
        super(cause);
    }
}
