package com.godfunc.shardingsphere.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis-plus 配置
 */
@Configuration
@MapperScan(basePackages = "com.godfunc.**.mapper")
public class MybatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    @Bean
    public IdentifierGenerator identifierGenerator(@Value("${workId}") Long workId,
                                                   @Value("${dataCenterId}")Long dataCenterId,
                                                   @Value("${databaseIdBits}") Long databaseIdBits,
                                                   @Value("${tableBits}") Long tableBits) {
        return new ShardingIdentifierGeneratorWrapper(workId, dataCenterId, databaseIdBits, tableBits);
    }
}
