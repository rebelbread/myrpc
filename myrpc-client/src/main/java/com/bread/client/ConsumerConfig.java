package com.bread.client;

import com.bread.registry.ServiceDiscovery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhiwj
 * @date 2019/6/18
 */
@Configuration
@AutoConfigureOrder(Integer.MIN_VALUE)
public class ConsumerConfig {

    @Value("${registryAddress}")
    private String registryAddress;

    @Bean
    public ServiceDiscovery serviceDiscovery() {
        return new ServiceDiscovery(registryAddress);
    }

    @Bean
    public RpcProxy rpcProxy() {
        return new RpcProxy(serviceDiscovery());
    }

}
