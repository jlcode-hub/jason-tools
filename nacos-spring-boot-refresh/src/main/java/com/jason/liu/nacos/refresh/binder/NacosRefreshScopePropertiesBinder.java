package com.jason.liu.nacos.refresh.binder;

import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.spring.beans.factory.annotation.ConfigServiceBeanBuilder;
import com.alibaba.nacos.spring.context.event.config.EventPublishingConfigService;
import com.alibaba.nacos.spring.context.event.config.NacosConfigEvent;
import com.alibaba.nacos.spring.context.event.config.NacosConfigMetadataEvent;
import com.alibaba.nacos.spring.context.properties.config.NacosConfigurationPropertiesBinder;
import com.alibaba.nacos.spring.core.env.NacosPropertySource;
import com.alibaba.nacos.spring.util.NacosUtils;
import com.jason.liu.nacos.refresh.annotation.NacosRefreshScope;
import com.jason.liu.nacos.refresh.event.NacosContentLoadEvent;
import com.jason.liu.nacos.refresh.event.NacosRefreshScopePropertiesBeanBoundEvent;
import com.jason.liu.nacos.refresh.utils.ConfigTypeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationBeanFactoryMetadata;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

import static com.alibaba.nacos.spring.util.NacosBeanUtils.getConfigServiceBeanBuilder;
import static com.alibaba.nacos.spring.util.NacosUtils.getContent;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.core.annotation.AnnotationUtils.getAnnotationAttributes;
import static org.springframework.util.StringUtils.hasText;

/**
 * @author: meng.liu
 * @date: 2020/8/5
 * TODO:
 */
public class NacosRefreshScopePropertiesBinder extends NacosConfigurationPropertiesBinder {

    public static final String BEAN_NAME = "JasonTools$$nacosRefreshScopePropertiesBinder";

    private static final Logger logger = LoggerFactory
            .getLogger(NacosRefreshScopePropertiesBinder.class);

    private final ConfigurableApplicationContext applicationContext;

    private StandardEnvironment standardEnvironment = new StandardEnvironment();

    private final Environment environment;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final ConfigServiceBeanBuilder configServiceBeanBuilder;

    private NacosConfigProperties nacosConfigProperties;

    private ConfigurationBeanFactoryMetadata beanFactoryMetadata;

    protected NacosRefreshScopePropertiesBinder(
            ConfigurableApplicationContext applicationContext) {
        super(applicationContext);
        Assert.notNull(applicationContext,
                "ConfigurableApplicationContext must not be null!");
        this.applicationContext = applicationContext;
        this.environment = applicationContext.getEnvironment();
        this.applicationEventPublisher = applicationContext;
        this.beanFactoryMetadata = applicationContext.getBean(
                ConfigurationBeanFactoryMetadata.BEAN_NAME,
                ConfigurationBeanFactoryMetadata.class);
        this.configServiceBeanBuilder = getConfigServiceBeanBuilder(applicationContext);
    }

    @Override
    protected void bind(Object bean, String beanName) {

        NacosRefreshScope scope = findAnnotation(bean.getClass(),
                NacosRefreshScope.class);

        ConfigurationProperties properties = findAnnotation(bean.getClass(),
                ConfigurationProperties.class);

        bind(bean, beanName, scope, properties);

    }

    protected void bind(final Object bean, final String beanName,
                        final NacosRefreshScope nacosRefreshScope, ConfigurationProperties configurationProperties) {

        Assert.notNull(bean, "Bean must not be null!");

        Assert.notNull(nacosRefreshScope, "NacosRefreshScope must not be null!");

        Assert.notNull(configurationProperties, "ConfigurationProperties must not be null!");

        final ConfigService configService = configServiceBeanBuilder
                .build(nacosRefreshScope.properties());
        String dataId = this.dataId(nacosRefreshScope);
        String groupId = this.groupId(nacosRefreshScope);
        String fileType = NacosUtils.readFileExtension(dataId);
        String type = ConfigTypeUtils.isConfigType(fileType) ? fileType : nacosRefreshScope.type().getType();
        // Add a Listener if auto-refreshed
        if (nacosRefreshScope.value()) {
            Listener listener = new AbstractListener() {
                @Override
                public void receiveConfigInfo(String config) {
                    doBind(bean, beanName, dataId, groupId, type, nacosRefreshScope, configurationProperties, config,
                            configService);
                }
            };

            try {//
                if (configService instanceof EventPublishingConfigService) {
                    ((EventPublishingConfigService) configService).addListener(dataId,
                            groupId, type, listener);
                } else {
                    configService.addListener(dataId, groupId, listener);
                }
            } catch (NacosException e) {
                if (logger.isErrorEnabled()) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        String content = getContent(configService, dataId, groupId);
        publishContentLoadEvent(dataId, groupId, nacosRefreshScope, content, configService);
        if (hasText(content)) {
            doBind(bean, beanName, dataId, groupId, type, nacosRefreshScope, configurationProperties, content,
                    configService);
        }
    }

    protected void doBind(Object bean, String beanName, String dataId, String groupId,
                          String configType, NacosRefreshScope properties, ConfigurationProperties configurationProperties, String content,
                          ConfigService configService) {
        String name = "nacos-refreshScope-" + beanName;
        NacosPropertySource propertySource = new NacosPropertySource(name, dataId,
                groupId, content, configType);
        standardEnvironment.getPropertySources().addLast(propertySource);
        Binder binder = Binder.get(standardEnvironment);
        ResolvableType type = getBeanType(bean, beanName);
        Bindable<?> target = Bindable.of(type).withExistingValue(bean);
        binder.bind(configurationProperties.prefix(), target);
        publishBoundEvent(bean, beanName, dataId, groupId, properties, content,
                configService);
        publishMetadataEvent(bean, beanName, dataId, groupId, properties);
        standardEnvironment.getPropertySources().remove(name);
    }

    private ResolvableType getBeanType(Object bean, String beanName) {
        Method factoryMethod = this.beanFactoryMetadata.findFactoryMethod(beanName);
        if (factoryMethod != null) {
            return ResolvableType.forMethodReturnType(factoryMethod);
        }
        return ResolvableType.forClass(bean.getClass());
    }

    protected void publishBoundEvent(Object bean, String beanName, String dataId,
                                     String groupId, NacosRefreshScope nacosRefreshScope, String content,
                                     ConfigService configService) {
        NacosConfigEvent event = new NacosRefreshScopePropertiesBeanBoundEvent(
                configService, dataId, groupId, bean, beanName, nacosRefreshScope, content);
        applicationEventPublisher.publishEvent(event);
    }

    protected void publishContentLoadEvent(String dataId, String groupId,
                                           NacosRefreshScope nacosRefreshScope,
                                           String content,
                                           ConfigService configService) {
        NacosConfigEvent event = new NacosContentLoadEvent(
                configService, dataId, groupId, content, nacosRefreshScope);
        applicationEventPublisher.publishEvent(event);
    }

    protected void publishMetadataEvent(Object bean, String beanName, String dataId,
                                        String groupId, NacosRefreshScope nacosRefreshScope) {

        NacosProperties nacosProperties = nacosRefreshScope.properties();

        NacosConfigMetadataEvent metadataEvent = new NacosConfigMetadataEvent(nacosRefreshScope);

        // Nacos Metadata
        metadataEvent.setDataId(dataId);
        metadataEvent.setGroupId(groupId);
        Properties resolvedNacosProperties = configServiceBeanBuilder
                .resolveProperties(nacosProperties);
        Map<String, Object> nacosPropertiesAttributes = getAnnotationAttributes(
                nacosProperties);
        metadataEvent.setNacosPropertiesAttributes(nacosPropertiesAttributes);
        metadataEvent.setNacosProperties(resolvedNacosProperties);

        // Bean Metadata
        Class<?> beanClass = bean.getClass();
        metadataEvent.setBeanName(beanName);
        metadataEvent.setBean(bean);
        metadataEvent.setBeanType(beanClass);
        metadataEvent.setAnnotatedElement(beanClass);

        // Publish event
        applicationEventPublisher.publishEvent(metadataEvent);
    }

    private String groupId(NacosRefreshScope nacosRefreshScope) {
        if (StringUtils.isBlank(nacosRefreshScope.groupId())) {
            return this.getConfigProperties().getGroup();
        } else {
            return NacosUtils.readFromEnvironment(nacosRefreshScope.groupId(),
                    environment);
        }
    }

    private String dataId(NacosRefreshScope nacosRefreshScope) {
        if (StringUtils.isBlank(nacosRefreshScope.dataId())) {
            return this.getConfigProperties().getDataId();
        } else {
            return NacosUtils.readFromEnvironment(nacosRefreshScope.groupId(),
                    environment);
        }
    }

    private NacosConfigProperties getConfigProperties() {
        if (null != nacosConfigProperties) {
            return nacosConfigProperties;
        }
        nacosConfigProperties = applicationContext.getBean(NacosConfigProperties.class);
        Assert.notNull(nacosConfigProperties, "Nacos config properties cannot be null.");
        return nacosConfigProperties;
    }
}
