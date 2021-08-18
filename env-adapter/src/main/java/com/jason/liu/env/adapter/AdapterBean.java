package com.jason.liu.env.adapter;

import java.lang.annotation.*;

/**
 * 采用Bean定义的方式进行加载
 *
 * @author meng.liu
 * @version v1.0
 * @date 2021-07-07 10:10:22
 * @todo
 * @see EnableAdapter
 * @see EnvironmentAdapterRegister
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AdapterBean {

    /**
     * 适配环境
     *
     * @return
     */
    String value() default "default";

    ;

}
