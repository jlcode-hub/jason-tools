package com.jason.liu.callback;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * @author: meng.liu
 * @date: 2021/4/21
 * TODO:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({InvokerCallbackImportSelector.class})
public @interface EnableInvokerCallback {


    /**
     * 优先级
     *
     * @return
     */
    int order() default Ordered.HIGHEST_PRECEDENCE + 10;


    /**
     * 代理类方式，当前只能使用动态代理Proxy
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
