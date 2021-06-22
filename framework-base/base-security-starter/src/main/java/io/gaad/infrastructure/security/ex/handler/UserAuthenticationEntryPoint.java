package io.gaad.infrastructure.security.ex.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gaad.infrastructure.common.kit.ResultBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: framework-jshyun
 * @author: Tokey
 * @create: 2019-12-17 19:40
 */
public class UserAuthenticationEntryPoint extends OAuth2AuthenticationEntryPoint {

    /**
     * token不正确的时候，报这个异常
     * @param request
     * @param response
     * @param e
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        encapsulateResult(response, e);
    }

    private void encapsulateResult(HttpServletResponse response, AuthenticationException e) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(200);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), ResultBean.authFeiled().setData(e.getMessage()));
    }

}