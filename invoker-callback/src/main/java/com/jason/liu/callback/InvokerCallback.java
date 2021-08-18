package com.jason.liu.callback;

import java.lang.annotation.*;

/**
 * @author: meng.liu
 * @date: 2021/4/21
 * TODO: 注册回调
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface InvokerCallback {

    /**
     * 当出现异常时不执行
     *
     * @return
     */
    Class<? extends Throwable>[] noInvokeFor() default {};

}
