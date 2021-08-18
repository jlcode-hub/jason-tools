package com.jason.liu.counter;

import com.jason.liu.counter.utils.ValueExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: meng.liu
 * @date: 2021/1/18
 * TODO:
 */
@Slf4j
public class Counters implements ApplicationListener<ApplicationStartedEvent> {

    public static final String BEAN_NAME = "JasonToolsCustom$$counters";

    private Map<Method, MethodCounter> methodCounterMap = new ConcurrentHashMap<>();

    public void register(MethodCounter methodCounter) {
        if (methodCounterMap.containsKey(methodCounter.getMethod())) {
            return;
        }
        methodCounterMap.put(methodCounter.getMethod(), methodCounter);
    }

    public Collection<MethodCounter> getAll() {
        return methodCounterMap.values();
    }

    public MethodCounter get(Method method) {
        return methodCounterMap.get(method);
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if (methodCounterMap.isEmpty()) {
            return;
        }
        ApplicationContext context = event.getApplicationContext();
        try {
            for (MethodCounter methodCounter : methodCounterMap.values()) {
                Count count = methodCounter.getCount();
                Class<? extends ICountRule> ruleClass = count.rule();
                ICountRule rule = ruleClass.newInstance();
                ICounter counter = context.getBean(count.counter());
                methodCounter.setRule(rule);
                methodCounter.setCounter(counter);
                methodCounter.setValueExpression(new ValueExpression(count.key(), methodCounter.getMethod()));
            }
            log.info("init counter success.");
        } catch (Exception e) {
            log.warn("init method counter exception", e);
            throw new RuntimeException(e);
        }
    }
}
