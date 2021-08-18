package com.jason.liu.counter;

import java.lang.annotation.*;

/**
 * @author: meng.liu
 * @date: 2021/1/18
 * TODO:
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Count {
    /**
     * 标识
     *
     * @return
     */
    String key();

    /**
     * 计数规则，用来处理调用接口时的计数方式
     * 服务启动时会通过反射为每一个业务接口创建一个该类型实例
     *
     * @return
     */
    Class<? extends ICountRule> rule();

    /**
     * 计数器Bean
     * 需要初始化为SpringBean通过Spring上下文获取
     *
     * @return
     */
    Class<? extends ICounter> counter();
}
