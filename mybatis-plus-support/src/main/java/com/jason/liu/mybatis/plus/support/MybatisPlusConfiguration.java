package com.jason.liu.mybatis.plus.support;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 *
 * @author ：ljf
 * @date ：Created in 2021/5/13 19:53
 */
@Configuration
public class MybatisPlusConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * MybatisPlus的id生成器
     *
     * @param distributedIdGenerator
     * @return
     */
    @Bean
    public MybatisIdentifierGenerator identifierGenerator(DistributedIdGenerator distributedIdGenerator) {
        return new MybatisIdentifierGenerator(distributedIdGenerator);
    }


    @Bean
    public MySqlTypeHandler codeEnumTypeHandler() {
        return new MySqlTypeHandler();
    }
}
