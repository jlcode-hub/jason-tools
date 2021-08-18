package com.jason.liu.env.adapter;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-07-01 10:43:18
 * @todo
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(AdapterCondition.class)
public @interface Adapter {
    /**
     * 适配环境
     *
     * @return
     */
    String value() default "default";

}
