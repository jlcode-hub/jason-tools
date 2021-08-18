package com.jason.liu.mybatis.plus.support.annotation;

import java.lang.annotation.*;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-05-27 20:10:53
 * @todo 分布式ID中注册的ID字段名称
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IdGenName {
    /**
     * 分布式ID中注册的ID字段名称
     *
     * @return
     */
    String value();

}
