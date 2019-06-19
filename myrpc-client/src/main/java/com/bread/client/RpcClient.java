package com.bread.client;

import com.alibaba.fastjson.JSON;
import com.bread.handler.RpcDecoder;
import com.bread.handler.RpcEncoder;
import com.bread.pojo.RpcRequest;
import com.bread.pojo.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @author zhiwj
 * @date 2019/6/18
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse>{

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private String host;
    private Integer port;
    private RpcResponse response;
    private CountDownLatch cdl = new CountDownLatch(1);

    public RpcClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public RpcResponse send(RpcRequest request) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new RpcEncoder(RpcRequest.class));
                            pipeline.addLast(new RpcDecoder(RpcResponse.class));
                            // 处理请求
                            pipeline.addLast(RpcClient.this);
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true)
            ;
            ChannelFuture future = b.connect(host, port).sync();
            future.channel().writeAndFlush(request).sync();
            LOGGER.info("发送请求成功, request:{}", JSON.toJSONString(request));
            cdl.await();
            if (response != null) {
                future.channel().closeFuture().sync();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
        return response;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) throws Exception {
        this.response = response;
        cdl.countDown();
    }
}