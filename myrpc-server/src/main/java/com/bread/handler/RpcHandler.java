package com.bread.handler;

import com.bread.pojo.RpcRequest;
import com.bread.pojo.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author zhiwj
 * @date 2019/6/12
 */
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {


    private Map<String, Object> handlerMap;

    public RpcHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            String className = request.getClassName();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();

            Object serviceBean = handlerMap.get(className);
            Class<?> serviceClass = serviceBean.getClass();
            Method method = serviceClass.getMethod(methodName, parameterTypes);

            Object result = method.invoke(serviceBean, parameters);
            response.setResult(result);
        } catch (Throwable throwable) {
            response.setError(throwable);
        }
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
