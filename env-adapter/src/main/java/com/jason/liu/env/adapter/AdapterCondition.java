package com.jason.liu.env.adapter;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-07-07 09:32:32
 * @todo
 */
public class AdapterCondition implements Condition {

    private static final String ENV_CONFIG = "environment.adapt.type";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(Adapter.class.getName());
        if (CollectionUtils.isEmpty(attributes)) {
            return false;
        }
        String env = (String) attributes.get("value");
        return this.configurationEnv(context).equals(env);
    }

    private String configurationEnv(ConditionContext context) {
        return context.getEnvironment().getProperty(ENV_CONFIG, "default");
    }
}
