package com.jason.liu.time.consume;


import com.jason.liu.time.statistics.CallInfo;
import com.jason.liu.time.statistics.StatisticProperties;
import com.jason.liu.time.utils.SystemClock;
import com.jason.liu.time.utils.TimeLogUtils;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.util.Optional;

/**
 * @author: meng.liu
 * @date: 2021/1/7
 * TODO:
 */
@Slf4j
public class TimeConsumeInterceptor implements MethodInterceptor, ApplicationListener<ApplicationStartedEvent> {

    public static final String BEAN_NAME = "JasonToolsCustom$$timeConsumeInterceptor";

    private boolean enabled;

    private TimeConsumers timeConsumers;

    private StatisticProperties statisticProperties;

    public TimeConsumeInterceptor(TimeConsumers timeConsumers) {
        this.timeConsumers = timeConsumers;
        this.statisticProperties = timeConsumers.getStatisticProperties();
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        this.enabled = BooleanUtils.isTrue(statisticProperties.getEnabled());
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        if (!this.enabled) {
            return methodInvocation.proceed();
        } else {
            boolean isException = false;
            long start = SystemClock.currentTimeMillis();
            try {
                return methodInvocation.proceed();
            } catch (Exception e) {
                isException = true;
                throw e;
            } finally {
                long end = SystemClock.currentTimeMillis();
                boolean finalIsException = isException;
                Optional.ofNullable(timeConsumers.get(methodInvocation.getMethod())).ifPresent(methodInfo -> {
                    if (statisticProperties.getPrintWindow()) {
                        CallInfo callInfo = CallInfo.builder()
                                .timeConsume(end - start)
                                .calledTimestamp(start)
                                .completedTimestamp(end)
                                .isSuccess(!finalIsException)
                                .build();
                        Optional.ofNullable(timeConsumers.getTimeConsumeWindows().get(methodInfo.getKey()))
                                .ifPresent(window -> window.add(callInfo));
                    }
                    if (statisticProperties.getPrintMethod() && methodInfo.getTimeConsume().printMethod()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        TimeLogUtils.printTitle(stringBuilder, methodInfo.getTitle());
                        TimeLogUtils.printInfo(stringBuilder, methodInfo, finalIsException, end - start);
                        TimeLogUtils.printSepLine(stringBuilder, methodInfo.getTitle());
                        log.info("\n{}", stringBuilder);
                    }
                });
            }
        }
    }

}
