package io.gaad.infrastructure.rpc.annotation;

import io.gaad.infrastructure.rpc.entity.RpcType;

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
