package io.gaad.infrastructure.rpc.config;

import io.gaad.infrastructure.rpc.client.RpcClientScannerRegistrar;
import io.gaad.infrastructure.rpc.entity.RpcMode;
import io.gaad.infrastructure.rpc.server.RpcServerPostProcessor;
import io.gaad.infrastructure.rpc.annotation.EnableSimpleRpc;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RpcDeferredImportSelector
 *
 * @author toby
 */
@Order
public class RpcDeferredImportSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        List<String> definitionRegistrars = new ArrayList<>();
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableSimpleRpc.class.getCanonicalName());
        if (annotationAttributes != null) {
            RpcMode[] rpcModes = (RpcMode[]) annotationAttributes.get("mode");
            for (RpcMode rpcMode : rpcModes) {
                switch (rpcMode) {
                    case RPC_CLIENT:
                        definitionRegistrars.add(RpcClientScannerRegistrar.class.getName());
                        break;
                    case RPC_SERVER:
                        definitionRegistrars.add(RpcServerPostProcessor.class.getName());
                        break;
                    default:
                        break;
                }
            }
        }
        return definitionRegistrars.toArray(new String[0]);
    }

}
