package com.jason.liu.verification.code;

import com.jason.liu.verification.code.constants.ImageType;
import com.jason.liu.verification.code.extractor.CookiesRequestIdExtractor;
import com.jason.liu.verification.code.generator.VerificationCodeGenerator;
import com.jason.liu.verification.code.generator.paint.JpegCodePaint;
import com.jason.liu.verification.code.generator.rules.ArithmeticRule;
import com.jason.liu.verification.code.generator.rules.RandomCharRule;
import com.jason.liu.verification.code.pool.CodeGeneratorImpl;
import com.jason.liu.verification.code.pool.ICodeBufferPool;
import com.jason.liu.verification.code.pool.ICodeStore;
import com.jason.liu.verification.code.pool.buffer.MemoryBufferPool;
import com.jason.liu.verification.code.pool.buffer.RedisBufferPool;
import com.jason.liu.verification.code.pool.store.MemoryStore;
import com.jason.liu.verification.code.pool.store.RedisStore;
import com.jason.liu.verification.code.properties.CodeProperty;
import com.jason.liu.verification.code.validator.IgnoreCaseCharEqualsValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO:
 */
@Configuration
@EnableConfigurationProperties(CodeProperty.class)
public class VerificationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ICodeRule.class)
    public ICodeRule codeRule(CodeProperty codeProperty) {
        switch (codeProperty.getRuleType()) {
            case ARITHMETIC:
                return new ArithmeticRule(codeProperty.getArithmeticRange());
            case RANDOM_CHAR:
            default:
                return new RandomCharRule(codeProperty.getRandomCharLength());
        }
    }

    @Bean
    @ConditionalOnMissingBean(ICodePaint.class)
    public ICodePaint codePaint(CodeProperty codeProperty) {
        ImageType type = codeProperty.getImageType();
        switch (type) {
            case JPEG:
            default:
                return new JpegCodePaint(codeProperty.getImageSizes().getOrDefault(type, new CodeProperty.ImageSize()));
        }
    }

    @Bean
    @ConditionalOnMissingBean(ICodeGenerator.class)
    public ICodeGenerator codeGenerator(ICodeRule codeRule, ICodePaint codePaint) {
        return new VerificationCodeGenerator(codeRule, codePaint);
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
    @ConditionalOnMissingBean(ICodeBufferPool.class)
    public ICodeBufferPool redisCodeBufferPool(ICodeGenerator codeGenerator, RedisTemplate<String, Object> redisTemplate) {
        return new RedisBufferPool(redisTemplate, codeGenerator.generatorName());
    }

    @Bean
    @ConditionalOnMissingBean(ICodeBufferPool.class)
    public ICodeBufferPool memoryCodeBufferPool(ICodeGenerator codeGenerator) {
        return new MemoryBufferPool();
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
    @ConditionalOnMissingBean(ICodeStore.class)
    public ICodeStore redisCodeStore(CodeProperty codeProperty,
                                     RedisTemplate<String, Object> redisTemplate) {
        return new RedisStore(codeProperty.getExpire(), redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(ICodeStore.class)
    public ICodeStore memoryCodeStore(CodeProperty codeProperty) {
        return new MemoryStore(codeProperty.getExpire());
    }

    @Bean
    @ConditionalOnMissingBean(IValidator.class)
    public IValidator<String> validator() {
        return new IgnoreCaseCharEqualsValidator();
    }

    @Bean
    @ConditionalOnMissingBean(IRequestIdExtractor.class)
    public IRequestIdExtractor requestIdExtractor() {
        return new CookiesRequestIdExtractor();
    }

    @Bean
    public CodeGeneratorImpl codeGeneratorImpl(CodeProperty codeProperty,
                                               ICodeGenerator codeGenerator,
                                               ICodeBufferPool codeBufferPool) {
        return new CodeGeneratorImpl(codeProperty, codeGenerator, codeBufferPool);
    }

    @Bean
    @ConditionalOnMissingBean(VerificationCodeTemplate.class)
    public VerificationCodeTemplate<String> verificationCodeTemplate(CodeGeneratorImpl codeGeneratorImpl,
                                                                     ICodeStore codeStore,
                                                                     IValidator<String> validator,
                                                                     IRequestIdExtractor requestIdExtractor) {
        return new VerificationCodeTemplate<>(codeGeneratorImpl, codeStore, validator, requestIdExtractor);
    }
}
