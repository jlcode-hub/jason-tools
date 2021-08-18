package com.jason.liu.warning.notice;

/**
 * 通知幂等校验
 *
 * @author meng.liu
 * @version 1.0
 * @date 2021-07-15 14:21:49
 */
public interface IdempotentNotice {

    /**
     * 是否已经发送
     *
     * @param noticeId
     * @return true:已发送，false:未发送
     */
    boolean isSent(String noticeId);

}
