package net.unsun.infrastructure.security.autoconfigure;

import net.unsun.infrastructure.security.config.CustomResourceServerConfigurerAdapter;
import net.unsun.infrastructure.security.config.CustomSecurityProperties;
import net.unsun.infrastructure.security.config.CustomWebSecurityConfigurerAdapter;
import net.unsun.infrastructure.security.config.GlobalCorsConfig;
import net.unsun.infrastructure.security.config.TokenFeignClientInterceptor;
import net.unsun.infrastructure.security.ex.global.GlobalExceptionHandler;
import net.unsun.infrastructure.security.ibatis.CustomMetaObjectHandler;
import net.unsun.infrastructure.security.ibatis.SqlInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @program: unsun-framework
 * @author: Tokey
 * @create: 2019-11-28 16:24
 */
@Configuration
@Import({CustomResourceServerConfigurerAdapter.class,
        TokenFeignClientInterceptor.class,
        GlobalExceptionHandler.class,
        GlobalCorsConfig.class,
        CustomWebSecurityConfigurerAdapter.class})
@EnableConfigurationProperties(CustomSecurityProperties.class)
public class CustomSecurityAutoConfiguration {

    /**
     * mybatis-plus处理时间等常规字段统一处理
     * @return
     */
    @Bean
    public CustomMetaObjectHandler customMetaObjectHandler() {
        return new CustomMetaObjectHandler();
    }

    /**
     * 非mybatis-plus 自有方法 ，xml里面自己写的方法 拦截 添加，更新时间
     *
     * @return DataScopeInterceptor
     */
    @Bean
    public SqlInterceptor sqlInterceptor() {
        return new SqlInterceptor();
    }

}