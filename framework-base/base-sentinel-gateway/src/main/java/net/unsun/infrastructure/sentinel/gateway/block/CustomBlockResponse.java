package net.unsun.infrastructure.sentinel.gateway.block;

import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.BlockResponse;

/**
 * @program: demo
 * @author: Tokey
 * @create: 2020-09-01 16:04
 */
public class CustomBlockResponse extends BlockResponse {
    public CustomBlockResponse(int code, String message, String route) {
        super(code, message, route);
    }

    @Override
    public String toString() {
        return "{\"tokey\":\"do it\"}";
    }
}