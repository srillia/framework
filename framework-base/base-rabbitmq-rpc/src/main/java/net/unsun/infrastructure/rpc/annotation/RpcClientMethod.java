package net.unsun.infrastructure.rpc.annotation;

import net.unsun.infrastructure.rpc.entity.RpcType;

import java.lang.annotation.*;

/**
 * RpcClientMethod
 *
 * @author toby
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcClientMethod {

    String value() default "";

    RpcType type() default RpcType.SYNC;
}
