package io.gaad.infrastructure.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * RpcClientScannerRegistrar
 *
 * @author toby
 */
public class RpcClientScannerRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientScannerRegistrar.class);

    private BeanFactory beanFactory;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        if (!AutoConfigurationPackages.has(this.beanFactory)) {
            LOGGER.debug("Could not determine auto-configuration package, automatic rpc-client scanning disabled.");
            return;
        }

        LOGGER.debug("Searching for rpc-client annotated with @RpcClient");

        List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
        if (LOGGER.isDebugEnabled()) {
            packages.forEach(pkg -> LOGGER.debug("Using auto-configuration base package '{}'", pkg));
        }

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RpcClientScannerConfigurer.class);
        builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(packages));
        registry.registerBeanDefinition(RpcClientScannerConfigurer.class.getName(), builder.getBeanDefinition());
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

}