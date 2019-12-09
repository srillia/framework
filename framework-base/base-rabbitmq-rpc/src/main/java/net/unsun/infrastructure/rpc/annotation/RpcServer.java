package net.unsun.infrastructure.rpc.annotation;

import net.unsun.infrastructure.rpc.entity.RpcType;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * RpcServer
 *
 * @author toby
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RpcServer {

    String value();

    int xMessageTTL() default 1000;

    int threadNum() default 1;

    RpcType[] type() default {RpcType.SYNC, RpcType.ASYNC};
}
