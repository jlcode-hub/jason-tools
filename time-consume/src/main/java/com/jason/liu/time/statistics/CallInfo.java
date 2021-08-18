package com.jason.liu.time.statistics;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author: meng.liu
 * @date: 2021/1/12
 * TODO:
 */
@Getter
@Builder
public class CallInfo implements Serializable {
    /**
     * 耗时
     */
    private long timeConsume;
    /**
     * 是否成功
     */
    private boolean isSuccess;
    /**
     * 调用时间戳
     */
    private long calledTimestamp;
    /**
     * 调用完成时间戳
     */
    private long completedTimestamp;

}
