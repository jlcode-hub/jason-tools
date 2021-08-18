package com.jason.liu.time.consume;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;

/**
 * @author: meng.liu
 * @date: 2021/1/8
 * TODO: 根据注解动态选择使用哪一种类型的代理模式
 */
public class TimeConsumerImportSelector extends AdviceModeImportSelector<EnableTimeConsume> {

    @Override
    protected String[] selectImports(AdviceMode adviceMode) {
        if (adviceMode == AdviceMode.PROXY) {
            return new String[]{AutoProxyRegistrar.class.getName(), TimeConsumeConfiguration.class.getName()};
        }
        return null;
    }
}
