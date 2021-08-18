package com.jason.liu.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-06-28 14:42:05
 * @todo
 */
public class RedisTemplateSupportFactory {

    public static final String BEAN_NAME = "redisTemplateSupportFactory";

    private RedisTemplateSupport primaryRedisTemplate;

    private StringRedisTemplateSupport primaryStringRedisTemplateSupport;

    private final Map<String, RedisTemplateSupport> redisTemplateSupportCache = new HashMap<>();

    private final Map<String, StringRedisTemplateSupport> stringRedisTemplateSupportCache = new HashMap<>();

    public void registerTemplate(BaseRedisTemplateSupport<?> baseRedisTemplateSupport) {
        if (baseRedisTemplateSupport instanceof StringRedisTemplateSupport) {
            StringRedisTemplateSupport stringRedisTemplateSupport = (StringRedisTemplateSupport) baseRedisTemplateSupport;
            this.stringRedisTemplateSupportCache.put(baseRedisTemplateSupport.getId(), stringRedisTemplateSupport);
            if (baseRedisTemplateSupport.getIsPrimary()) {
                this.primaryStringRedisTemplateSupport = stringRedisTemplateSupport;
            }
        } else if (baseRedisTemplateSupport instanceof RedisTemplateSupport) {
            RedisTemplateSupport redisTemplateSupport = (RedisTemplateSupport) baseRedisTemplateSupport;
            this.redisTemplateSupportCache.put(redisTemplateSupport.getId(), redisTemplateSupport);
            if (baseRedisTemplateSupport.getIsPrimary()) {
                this.primaryRedisTemplate = redisTemplateSupport;
            }
        }
    }

    public Operator<RedisTemplateSupport> operateTemplate(String key) {
        return new Operator<>(key, this.redisTemplateSupportCache);
    }

    public Operator<StringRedisTemplateSupport> operateStringTemplate(String key) {
        return new Operator<>(key, this.stringRedisTemplateSupportCache);
    }

    public Operator<RedisTemplateSupport> operateTemplate() {
        return new Operator<>(this.primaryRedisTemplate);
    }

    public Operator<StringRedisTemplateSupport> operateStringTemplate() {
        return new Operator<>(this.primaryStringRedisTemplateSupport);
    }

    /**
     * Redis操作器
     *
     * @param <Template>
     */
    public static class Operator<Template extends BaseRedisTemplateSupport> {

        private Template redisTemplateSupport;

        Operator(Template redisTemplateSupport) {
            this.redisTemplateSupport = redisTemplateSupport;
        }

        Operator(String key, Map<String, Template> cache) {
            this.redisTemplateSupport = Optional.ofNullable(cache.get(key)).orElseThrow(() -> new IllegalArgumentException("cannot find RedisTemplateSupport instance which named " + key));
        }

        public void invoke(Consumer<Template> consumer) {
            consumer.accept(this.redisTemplateSupport);
        }

        public <T> T call(Function<Template, T> function) {
            return function.apply(this.redisTemplateSupport);
        }

        public Template get() {
            return this.redisTemplateSupport;
        }

    }

}
