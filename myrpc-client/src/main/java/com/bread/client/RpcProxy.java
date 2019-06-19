package com.bread.client;

import com.bread.pojo.RpcRequest;
import com.bread.pojo.RpcResponse;
import com.bread.registry.ServiceDiscovery;
import com.bread.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author zhiwj
 * @date 2019/6/18
 */
public class RpcProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);

    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<?> interfaceClass){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                (proxy, method, args) -> {
                    String address = serviceDiscovery.discover();
                    if (StringUtils.isEmpty(address)) {
                        return null;
                    }
                    String[] addressArr = address.split(":");
                    RpcClient rpcClient = new RpcClient(addressArr[0], Integer.parseInt(addressArr[1]));
                    RpcRequest request = new RpcRequest();
                    request.setRequestId(UUID.randomUUID().toString());
                    request.setClassName(method.getDeclaringClass().getName());
                    request.setMethodName(method.getName());
                    request.setParameterTypes(method.getParameterTypes());
                    request.setParameters(args);
                    RpcResponse response = rpcClient.send(request);
                    if (response.getError() != null) {
                        throw response.getError();
                    } else {
                        return response.getResult();
                    }
                }
        );
    }


}
