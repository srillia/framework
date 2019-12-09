package net.unsun.infrastructure.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 日志拦截器
 *
 * @author srillia(srillia@coldweaponera.com)
 * @version 1.0.0
 * @since 15:53 2017/07/03
 */
@Aspect
public class LogInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 定义拦截规则：拦截controller/service包下面的所有类中，有@LogAspect注解的方法。
     */
    @Pointcut("(@annotation(net.unsun.infrastructure.logging.LogAspect))")
    public void methodPointcut() {
    }

    /**
     * 控制层拦截器具体实现
     *
     * @param proceedingJoinPoint 拦截点
     * @return 被拦截方法返回值
     */
    @Around("methodPointcut()")
    public Object interceptor(ProceedingJoinPoint proceedingJoinPoint) {
        long beginTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();          //获取被拦截的方法
        //String methodName = method.getName();         //获取被拦截的方法名
        Object[] args = proceedingJoinPoint.getArgs();  //保存所有请求参数，用于输出到日志中
        Object result;                                  //请求结果保存
        try {
            logger.info("处理请求[{}]开始, 参数：{}", method, args);
            // 一切正常的情况下，继续执行被拦截的方法
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            logger.error("处理请求[{}]时发生异常", method, e);
            throw new RuntimeException(e);
        } finally {
            logger.debug("处理请求[{}]结束，耗时：{}ms", method, System.currentTimeMillis() - beginTime);
        }
        return result;
    }
}
