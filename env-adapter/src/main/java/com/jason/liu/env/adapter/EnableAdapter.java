package com.jason.liu.env.adapter;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-07-05 10:58:10
 * @todo
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(AdapterBean.class)
})
@Import(EnvironmentAdapterRegister.class)
public @interface EnableAdapter {

    /**
     * 扫描目录
     *
     * @return
     */
    String[] scanBasePackages();

}
