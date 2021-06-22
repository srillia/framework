package io.gaad.infrastructure.security.ex.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gaad.infrastructure.common.kit.ResultBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: framework-jshyun
 * @author: Tokey
 * @create: 2019-12-17 19:45
 */
public class UserAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * 配置了GlobalExceptionHandler,这个类将失效
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ade) throws IOException, ServletException {
        encapsulateResult(response, ade);
    }

    private void encapsulateResult(HttpServletResponse response, AccessDeniedException ade) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(200);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), ResultBean.notPermited().setData(ade.getMessage()));
    }
}