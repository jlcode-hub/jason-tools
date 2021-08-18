package com.jason.liu.counter;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author: meng.liu
 * @date: 2021/1/18
 * TODO:
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({CounterImportSelector.class})
public @interface EnableCounter {

    /**
     * 计数器实例唯一标识
     * 注意，当该字段为分布式锁隔离前缀
     *
     * @return
     */
    String name();

    /**
     * 代理类方式
     *
     * @return
     */
    AdviceMode mode() default AdviceMode.PROXY;

    /**
     * 扫描的包
     *
     * @return
     */
    String[] basePackages();

}
