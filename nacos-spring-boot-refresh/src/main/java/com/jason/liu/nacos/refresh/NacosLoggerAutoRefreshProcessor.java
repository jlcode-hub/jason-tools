package com.jason.liu.nacos.refresh;

import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.jason.liu.nacos.refresh.utils.NacosLoggingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author: meng.liu
 * @date: 2020/8/20
 * TODO:
 */
public class NacosLoggerAutoRefreshProcessor {

    private static final Logger log = LoggerFactory.getLogger(NacosLoggerAutoRefreshProcessor.class);

    public static final String BEAN_NAME = "JasonTools$$LoggerAutoRefreshProcessor";

    private NacosLoggingContext nacosLoggingContext;

    @Autowired
    public NacosLoggerAutoRefreshProcessor(NacosLoggingContext nacosLoggingContext) {
        this.nacosLoggingContext = nacosLoggingContext;
    }

    @NacosConfigListener(dataId = "${nacos.config.data-id}", groupId = "${nacos.config.group}", timeout = 30000)
    public void onChange(Map<String, Object> properties) {
        this.nacosLoggingContext.refreshLogLevel(properties);
    }
}
