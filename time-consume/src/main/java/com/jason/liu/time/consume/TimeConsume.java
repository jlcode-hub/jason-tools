package com.jason.liu.time.consume;

import java.lang.annotation.*;

/**
 * @author: meng.liu
 * @date: 2021/1/7
 * TODO:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TimeConsume {

    /**
     * 时间统计标识
     *
     * @return
     */
    String key() default "";

    /**
     * 打印方法的调用信息
     *
     * @return
     */
    boolean printMethod() default true;

    /**
     * 统计周期时长 单位：秒
     *
     * @return
     */
    String period() default "${jason.tools.time-consume.period}";

    /**
     * 块时长 单位：秒
     *
     * @return
     */
    String block() default "${jason.tools.time-consume.block}";

}
