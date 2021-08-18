package com.jason.liu.nacos.refresh.annotation;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.api.config.ConfigType;

import java.lang.annotation.*;

/**
 * @author: meng.liu
 * @date: 2020/8/5
 * TODO:
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NacosRefreshScope {
    /**
     * enabled
     *
     * @return
     */
    boolean value() default true;


    /**
     * Nacos Group ID
     *
     * @return default value
     */
    String groupId() default "";

    /**
     * Nacos Data ID
     *
     * @return required value.
     */
    String dataId() default "";

    /**
     * config style
     *
     * @return default value is {@link ConfigType#PROPERTIES}
     */
    ConfigType type() default ConfigType.PROPERTIES;

    /**
     * Flag to indicate that when binding to this object fields with periods in their names should be ignored.
     *
     * @return the flag value (default false)
     */
    boolean ignoreNestedProperties() default false;


    /**
     * The {@link NacosProperties} attribute, If not specified, it will use global Nacos Properties.
     *
     * @return the default value is {@link NacosProperties}
     */
    NacosProperties properties() default @NacosProperties;
}
