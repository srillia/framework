package io.gaad.infrastructure.sentinel.gateway.block;

import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.BlockResponse;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackProvider;

/**
 * @program: demo
 * @author: Tokey
 * @create: 2020-09-01 15:54
 */
public class CustomSentinelBlockFallbackProvider implements ZuulBlockFallbackProvider {

    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public BlockResponse fallbackResponse(String route, Throwable cause) {

        return new CustomBlockResponse(200, "sentinel block", route);
    }
}
