package com.jason.liu.nacos.refresh.binder;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.common.utils.MD5Utils;
import com.alibaba.nacos.spring.context.event.config.NacosConfigReceivedEvent;
import com.jason.liu.nacos.refresh.annotation.NacosRefreshScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.*;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;

/**
 * @author: meng.liu
 * @date: 2020/8/5
 * TODO:
 */
public class NacosRefreshScopeBeanPostProcessor implements BeanFactoryAware, EnvironmentAware, BeanPostProcessor, ApplicationContextAware, ApplicationListener<NacosConfigReceivedEvent> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * The name of {@link NacosRefreshScopeBeanPostProcessor} Bean
     */
    public static final String BEAN_NAME = "JasonTools$$nacosRefreshScopeBeanPostProcessor";

    private ConfigurableApplicationContext applicationContext;

    private ConfigurableBeanFactory beanFactory;

    private Environment environment;

    private static final String PLACEHOLDER_PREFIX = "${";

    private static final String PLACEHOLDER_SUFFIX = "}";

    private static final String VALUE_SEPARATOR = ":";

    /**
     * placeholder, NacosRereshScopeValueTarget
     * {@link NacosRereshScopeValueTarget}
     */
    private Map<String, List<NacosRereshScopeValueTarget>> placeholderNacosValueTargetMap
            = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        doWithBean(bean, beanName);

        doWithMethods(bean, beanName);

        doWithFields(bean, beanName);

        return bean;
    }


    private void doWithBean(final Object bean, final String beanName) {
        NacosRefreshScope nacosRefreshScope = findAnnotation(
                bean.getClass(), NacosRefreshScope.class);
        ConfigurationProperties configurationProperties = findAnnotation(
                bean.getClass(), ConfigurationProperties.class);

        if (nacosRefreshScope != null && configurationProperties != null) {
            bind(bean, beanName, nacosRefreshScope, configurationProperties);
        }
    }

    private void doWithFields(final Object bean, final String beanName) {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            NacosRefreshScope nacosRefreshScope = getAnnotation(field, NacosRefreshScope.class);
            Value value = getAnnotation(field, Value.class);
            doWithAnnotation(beanName, bean, value, nacosRefreshScope, field.getModifiers(), null, field);
        });
    }

    private void doWithMethods(final Object bean, final String beanName) {
        ReflectionUtils.doWithMethods(bean.getClass(), method -> {
            NacosRefreshScope nacosRefreshScope = getAnnotation(method, NacosRefreshScope.class);
            Value value = getAnnotation(method, Value.class);
            doWithAnnotation(beanName, bean, value, nacosRefreshScope, method.getModifiers(), method, null);
        });
    }


    private void bind(Object bean, String beanName,
                      NacosRefreshScope nacosRefreshScope, ConfigurationProperties configurationProperties) {

        NacosRefreshScopePropertiesBinder binder;
        try {
            binder = applicationContext.getBean(
                    NacosRefreshScopePropertiesBinder.BEAN_NAME,
                    NacosRefreshScopePropertiesBinder.class);
            if (binder == null) {
                binder = new NacosRefreshScopePropertiesBinder(applicationContext);
            }

        } catch (Exception e) {
            binder = new NacosRefreshScopePropertiesBinder(applicationContext);
        }

        binder.bind(bean, beanName, nacosRefreshScope, configurationProperties);

    }

    private void doWithAnnotation(String beanName, Object bean, Value value, NacosRefreshScope nacosRefreshScope, int modifiers, Method method,
                                  Field field) {
        if (nacosRefreshScope != null && null != value) {
            if (Modifier.isStatic(modifiers)) {
                return;
            }

            if (nacosRefreshScope.value()) {
                String placeholder = resolvePlaceholder(value.value());

                if (placeholder == null) {
                    return;
                }

                NacosRereshScopeValueTarget nacosValueTarget = new NacosRereshScopeValueTarget(bean, beanName, method, field);
                put2ListMap(placeholderNacosValueTargetMap, placeholder, nacosValueTarget);
            }
        }
    }

    private String resolvePlaceholder(String placeholder) {
        if (!placeholder.startsWith(PLACEHOLDER_PREFIX)) {
            return null;
        }

        if (!placeholder.endsWith(PLACEHOLDER_SUFFIX)) {
            return null;
        }

        if (placeholder.length() <= PLACEHOLDER_PREFIX.length() + PLACEHOLDER_SUFFIX.length()) {
            return null;
        }

        int beginIndex = PLACEHOLDER_PREFIX.length();
        int endIndex = placeholder.length() - PLACEHOLDER_PREFIX.length() + 1;
        placeholder = placeholder.substring(beginIndex, endIndex);

        int separatorIndex = placeholder.indexOf(VALUE_SEPARATOR);
        if (separatorIndex != -1) {
            return placeholder.substring(0, separatorIndex);
        }

        return placeholder;
    }

    private <K, V> void put2ListMap(Map<K, List<V>> map, K key, V value) {
        List<V> valueList = map.get(key);
        if (valueList == null) {
            valueList = new ArrayList<V>();
        }
        valueList.add(value);
        map.put(key, valueList);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void onApplicationEvent(NacosConfigReceivedEvent nacosConfigReceivedEvent) {
        for (Map.Entry<String, List<NacosRereshScopeValueTarget>> entry : placeholderNacosValueTargetMap.entrySet()) {
            String key = environment.resolvePlaceholders(entry.getKey());
            String newValue = environment.getProperty(key);
            if (newValue == null) {
                continue;
            }
            List<NacosRereshScopeValueTarget> beanPropertyList = entry.getValue();
            for (NacosRereshScopeValueTarget target : beanPropertyList) {
                String md5String = MD5Utils.md5Hex(newValue, Constants.ENCODE);
                boolean isUpdate = !target.lastMD5.equals(md5String);
                if (isUpdate) {
                    target.updateLastMD5(md5String);
                    if (target.method == null) {
                        setField(target, newValue);
                    } else {
                        setMethod(target, newValue);
                    }
                }
            }
        }
    }

    private void setMethod(NacosRereshScopeValueTarget nacosValueTarget, String propertyValue) {
        Method method = nacosValueTarget.method;
        ReflectionUtils.makeAccessible(method);
        try {
            method.invoke(nacosValueTarget.bean, convertIfNecessary(method, propertyValue));

            if (logger.isDebugEnabled()) {
                logger.debug("Update value with {} (method) in {} (bean) with {}",
                        method.getName(), nacosValueTarget.beanName, propertyValue);
            }
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error(
                        "Can't update value with " + method.getName() + " (method) in "
                                + nacosValueTarget.beanName + " (bean)", e);
            }
        }
    }

    private void setField(final NacosRereshScopeValueTarget nacosValueTarget, final String propertyValue) {
        final Object bean = nacosValueTarget.bean;

        Field field = nacosValueTarget.field;

        String fieldName = field.getName();

        try {
            ReflectionUtils.makeAccessible(field);
            field.set(bean, convertIfNecessary(field, propertyValue));

            if (logger.isDebugEnabled()) {
                logger.debug("Update value of the {}" + " (field) in {} (bean) with {}",
                        fieldName, nacosValueTarget.beanName, propertyValue);
            }
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error(
                        "Can't update value of the " + fieldName + " (field) in "
                                + nacosValueTarget.beanName + " (bean)", e);
            }
        }
    }

    private Object convertIfNecessary(Field field, Object value) {
        TypeConverter converter = beanFactory.getTypeConverter();
        return converter.convertIfNecessary(value, field.getType(), field);
    }

    private Object convertIfNecessary(Method method, Object value) {
        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] arguments = new Object[paramTypes.length];

        TypeConverter converter = beanFactory.getTypeConverter();

        if (arguments.length == 1) {
            return converter.convertIfNecessary(value, paramTypes[0], new MethodParameter(method, 0));
        }

        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = converter.convertIfNecessary(value, paramTypes[i], new MethodParameter(method, i));
        }

        return arguments;
    }


    private static class NacosRereshScopeValueTarget {

        private final Object bean;

        private final String beanName;

        private final Method method;

        private final Field field;

        private String lastMD5;

        NacosRereshScopeValueTarget(Object bean, String beanName, Method method, Field field) {
            this.bean = bean;

            this.beanName = beanName;

            this.method = method;

            this.field = field;

            this.lastMD5 = "";
        }

        protected void updateLastMD5(String newMD5) {
            this.lastMD5 = newMD5;
        }

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
