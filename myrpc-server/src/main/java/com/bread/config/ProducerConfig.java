package com.bread.config;

import com.bread.registry.ServiceRegistry;
import com.bread.server.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhiwj
 * @date 2019/6/12
 */
@Configuration
public class ProducerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);

    @Value("${registryAddress}")
    private String registryAddress;

    @Value("${serverProduceAddress}")
    private String serverProduceAddress;

    @Bean
    public ServiceRegistry serviceRegistry(){
        ServiceRegistry registry = new ServiceRegistry(registryAddress);
        LOGGER.info("serviceRegistry connect ... [address:{}] ", registryAddress);
        return registry;
    }

    @Bean
    public RpcServer rpcServer(){
        RpcServer rpcServer = new RpcServer(serverProduceAddress, serviceRegistry());
        LOGGER.info("rpcServer start success ... [address : {}]", serverProduceAddress);
        return rpcServer;
    }

}
