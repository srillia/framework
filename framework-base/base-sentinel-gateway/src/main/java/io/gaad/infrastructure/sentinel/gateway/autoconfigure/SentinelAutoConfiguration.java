package io.gaad.infrastructure.sentinel.gateway.autoconfigure;

import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackProvider;
import io.gaad.infrastructure.sentinel.gateway.block.CustomSentinelBlockFallbackProvider;
import io.gaad.infrastructure.sentinel.gateway.init.NacosDataSourceInitFunc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @program: unsun-framework
 * @author: Tokey
 * @create: 2019-11-28 16:24
 */
@Configuration
@Import(NacosDataSourceInitFunc.class)
public class SentinelAutoConfiguration {

    @Bean
    public ZuulBlockFallbackProvider customSentinelBlockFallbackProvider() {
        return new CustomSentinelBlockFallbackProvider();
    }

}