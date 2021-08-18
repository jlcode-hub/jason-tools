package com.jason.liu.redis.support;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.jason.liu.redis.config.RedisKeySupport;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.io.IOException;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-05-26 15:59:26
 * @todo
 */
public class RedisTemplateForJackson2 extends AbstractRedisTemplateSerializerSupport<GenericJackson2JsonRedisSerializer> {

    public RedisTemplateForJackson2(RedisConnectionFactory redisConnectionFactory,
                                    RedisKeySupport redisKeySupport) {
        super(redisConnectionFactory, redisKeySupport);
    }

    @Override
    public GenericJackson2JsonRedisSerializer valueSerializerSupport() {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.registerModule(new SimpleModule().addSerializer(new NullValueSerializer()));
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return new GenericJackson2JsonRedisSerializer(om);
    }

    private static class NullValueSerializer extends StdSerializer<NullValue> {

        private static final long serialVersionUID = 1999052150548658808L;
        private final String classIdentifier;

        NullValueSerializer() {
            super(NullValue.class);
            this.classIdentifier = "@class";
        }

        @Override
        public void serialize(NullValue value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {

            jgen.writeStartObject();
            jgen.writeStringField(classIdentifier, NullValue.class.getName());
            jgen.writeEndObject();
        }
    }
}
