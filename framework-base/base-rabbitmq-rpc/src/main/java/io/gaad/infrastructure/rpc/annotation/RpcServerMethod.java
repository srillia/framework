package io.gaad.infrastructure.rpc.annotation;

import java.lang.annotation.*;

/**
 * RpcServerMethod
 *
 * @author toby
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcServerMethod {

    String value() default "";
}
