package net.unsun.infrastructure.security.ex.global;

import lombok.extern.slf4j.Slf4j;
import net.unsun.infrastructure.common.kit.ResultBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * @program: framework-jshyun
 * @author: Tokey
 * @create: 2019-12-17 15:35
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResultBean globalException(HttpServletResponse response, Exception ex){
        if(ex instanceof AccessDeniedException) {
            return ResultBean.notPermited().setData(ex.getMessage());
        }
        ex.printStackTrace();
        log.info("GlobalExceptionHandler捕获到异常信息");
        log.error("异常信息:" + ex);
        return ResultBean.systemBusy().setData(ex.getMessage());
    }

}