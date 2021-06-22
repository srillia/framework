package io.gaad.infrastructure.sentinel.gateway.block;

import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.BlockResponse;
import com.alibaba.fastjson.JSON;
import io.gaad.infrastructure.common.kit.ResultBean;

/**
 * @program: demo
 * @author: Tokey
 * @create: 2020-09-01 16:04
 */
public class CustomBlockResponse extends BlockResponse {

    private ResultBean resultBean = ResultBean.systemBusy();

    public CustomBlockResponse(int code, String message, String route) {
        super(code, message, route);
    }

    @Override
    public String toString() {
        resultBean.setMessage(getMessage()+",route: "+getRoute());
        return JSON.toJSONString(resultBean);
    }
}