package net.unsun.infrastructure.rpc.annotation;

import net.unsun.infrastructure.rpc.entity.RpcType;

import java.lang.annotation.*;

/**
 * RpcClient
 *
 * @author toby
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcClient {

    String value();

    int replyTimeout() default 2000;

    int maxAttempts() default 3;
}
