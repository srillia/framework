package net.unsun.infrastructure.sentinel.gateway.block;

import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.BlockResponse;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackProvider;
import com.alibaba.csp.sentinel.slots.block.BlockException;

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
        if (cause instanceof BlockException) {
            return new CustomBlockResponse(429, "tokey do it", route);
        } else {
            return new BlockResponse(500, "System Error", route);
        }
    }
}
