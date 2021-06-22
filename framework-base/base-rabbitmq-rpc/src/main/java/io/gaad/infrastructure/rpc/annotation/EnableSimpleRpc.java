package io.gaad.infrastructure.rpc.annotation;

import io.gaad.infrastructure.rpc.config.RpcDeferredImportSelector;
import io.gaad.infrastructure.rpc.entity.RpcMode;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * EnableSimpleRpc
 *
 * @author toby
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RpcDeferredImportSelector.class)
public @interface EnableSimpleRpc {

    RpcMode[] mode() default {RpcMode.RPC_CLIENT, RpcMode.RPC_SERVER};

}