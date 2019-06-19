package com.bread.handler;

import com.bread.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author zhiwj
 * @date 2019/6/12
 */
public class RpcDecoder extends ByteToMessageDecoder {
    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4) {
            // 少于一个int 不处理
            return;
        }
        byteBuf.markReaderIndex();
        // length为本次要接收的数据长度
        int length = byteBuf.readInt();
        if (length < 0) {
            ctx.close();
        }
        if (byteBuf.readableBytes() < length) {
            // 可读数据少于长度 可能还未接收完 把索引重置为刚刚mark的地方
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] dataArr = new byte[length];
        byteBuf.readBytes(dataArr);

//        String s = new String(dataArr, CharsetUtil.UTF_8);
//        list.add(JSON.parseObject(s, t.getClass()));
        Object obj = SerializationUtil.deserialize(dataArr, genericClass);
        list.add(obj);
    }
}
