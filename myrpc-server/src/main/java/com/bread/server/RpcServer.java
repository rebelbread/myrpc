package com.bread.server;

import com.bread.handler.RpcDecoder;
import com.bread.handler.RpcEncoder;
import com.bread.handler.RpcHandler;
import com.bread.pojo.RpcRequest;
import com.bread.pojo.RpcResponse;
import com.bread.registry.ServiceRegistry;
import com.bread.utils.RpcService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhiwj
 * @date 2019/6/12
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {

    private String serverProduceAddress;
    private ServiceRegistry serviceRegistry;

    private HashMap<String, Object> handlerMap;

    public RpcServer(String serverProduceAddress) {
        this.serverProduceAddress = serverProduceAddress;
    }

    public RpcServer(String serverProduceAddress, ServiceRegistry serviceRegistry) {
        this.serverProduceAddress = serverProduceAddress;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 这里获取所有提供服务的bean， 并进行统一的管理 : Map<interfaceName, serviceClass>
        Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        handlerMap = new HashMap<>(serviceMap.size());
        if (MapUtils.isNotEmpty(serviceMap)) {
            for (Map.Entry<String, Object> serviceClass : serviceMap.entrySet()) {
                String interfaceName = serviceClass.getValue().getClass().getAnnotation(RpcService.class).value().getName();
                handlerMap.put(interfaceName, serviceClass.getValue());
            }
        }
    }

    /**
    * 通过netty 注册、发布服务
    **/
    @Override
    public void afterPropertiesSet() throws Exception {
        // 在配置的地址 serverProduceAddress上 发布服务
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            // 对请求解码
                            pipeline.addLast(new RpcDecoder(RpcRequest.class));
                            // 对响应编码
                            pipeline.addLast(new RpcEncoder(RpcResponse.class));
                            // 处理请求
                            pipeline.addLast(new RpcHandler(handlerMap));

                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
            ;
            String[] split = serverProduceAddress.split(":");
            String host = split[0];
            Integer port = Integer.parseInt(split[1]);
            ChannelFuture f = b.bind(host, port).sync();
            // 向注册中心注册服务
            if (serviceRegistry != null) {
                serviceRegistry.register(serverProduceAddress);
            }
            f.channel().closeFuture().sync();
        }finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }

}
