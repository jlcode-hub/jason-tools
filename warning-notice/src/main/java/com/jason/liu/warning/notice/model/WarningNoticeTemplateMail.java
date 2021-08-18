package com.jason.liu.warning.notice.model;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.Assert;
import org.thymeleaf.util.FastStringWriter;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 告警通知模板邮件
 *
 * @author meng.liu
 * @version 1.0
 * @date 2021-07-15 12:52:10
 */
public class WarningNoticeTemplateMail {

    public static WaningInfoBuilder builder() {
        return new WaningInfoBuilder();
    }

    /**
     * 告警信息构建器
     *
     * @author meng.liu
     * @version 1.0
     * @date 2021-07-15 10:16:05
     */
    public static class WaningInfoBuilder {
        /**
         * 服务名称
         */
        private String serviceName;
        /**
         * 业务名称
         */
        private String businessName;
        /**
         * 处理建议
         */
        private String handlingAdvice;
        /**
         * 异常描述
         */
        private String cause;
        /**
         * 主要参数
         */
        private Map<String, Object> paramMap;
        /**
         * 异常栈
         */
        private Throwable throwable;
        /**
         * 环境信息
         */
        private String env;

        private WaningInfoBuilder() {
        }

        public TemplateMail buildTemplate() {
            return TemplateMail.of("warning-notice", this.build());
        }

        public Map<String, Object> build() {
            Map<String, Object> params = new HashMap<>(16);
            Assert.hasText(this.serviceName, "serviceName cannot be blank");
            params.put("serviceName", this.serviceName);
            Assert.hasText(this.businessName, "businessName cannot be blank");
            params.put("businessName", this.businessName);
            params.put("envName", this.env);
            params.put("handlingAdvice", Optional.ofNullable(this.handlingAdvice).orElse("Unknown"));
            params.put("exceptionCause", Optional.ofNullable(this.cause).orElseGet(() -> {
                if (null == throwable) {
                    return "Unknown";
                } else {
                    return throwable.getLocalizedMessage();
                }
            }));
            params.put("paramMap", this.paramMap);
            if (null != throwable) {
                FastStringWriter fstWriter = new FastStringWriter();
                PrintWriter pw = new PrintWriter(fstWriter);
                throwable.printStackTrace(pw);
                pw.flush();
                params.put("exceptionStack", fstWriter.toString());
            }
            params.put("exceptionTime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            return params;
        }

        public WaningInfoBuilder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public WaningInfoBuilder businessName(String businessName) {
            this.businessName = businessName;
            return this;
        }

        public WaningInfoBuilder handlingAdvice(String handlingAdvice) {
            this.handlingAdvice = handlingAdvice;
            return this;
        }

        public WaningInfoBuilder cause(String cause) {
            this.cause = cause;
            return this;
        }

        public WaningInfoBuilder paramMap(Map<String, Object> paramMap) {
            this.paramMap = paramMap;
            return this;
        }

        public WaningInfoBuilder throwable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public WaningInfoBuilder env(String env) {
            this.env = env;
            return this;
        }

    }

}
