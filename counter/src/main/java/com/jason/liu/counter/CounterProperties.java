package com.jason.liu.counter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: meng.liu
 * @date: 2021/1/20
 * TODO:
 */
@Data
@ConfigurationProperties(prefix = "jason.tools.counter")
public class CounterProperties {
    /**
     * 同步间隔
     * 单位：（秒）
     */
    private Long flushInterval = 3L;

    /**
     * 锁定时长
     * 单位：（秒）
     */
    private Long lockTime = 300L;
}
