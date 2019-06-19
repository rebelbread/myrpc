package com.bread;

import com.bread.client.RpcProxy;
import com.bread.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhiwj
 * @date 2019/6/19
 */
@Configuration
public class RpcConfig {

    @Autowired
    private RpcProxy rpcProxy;

    @Bean
    public HelloService helloService() {
        return rpcProxy.get(HelloService.class);
    }

}
