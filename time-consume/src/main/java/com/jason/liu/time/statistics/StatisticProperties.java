package com.jason.liu.time.statistics;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: meng.liu
 * @date: 2021/1/12
 * TODO:
 */
@Data
@ConfigurationProperties(prefix = "jason.tools.time-consume")
public class StatisticProperties {
    /**
     * 是否启用
     */
    private Boolean enabled = false;
    /**
     * 是否打印窗口信息
     */
    private Boolean printWindow = false;
    /**
     * 打印方法信息
     */
    private Boolean printMethod = false;
    /**
     * 统计打印间隔
     */
    private Long statisticPrintInterval = 30L;
    /**
     * 默认周期
     */
    private Integer defaultPeriod = 300;
    /**
     * 默认块大小
     */
    private Integer defaultBlock = 10;
}
