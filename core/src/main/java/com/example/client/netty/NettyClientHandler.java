package com.example.client.netty;

import common.message.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName NettyClientHandler
 * @Description 客户端处理器
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {


    /**
     * 处理接收到的RPC响应消息
     * @param ctx 通道处理器上下文，用于获取通道信息和进行相关操作
     * @param response 接收到的RPC响应对象
     * @throws Exception 可能抛出的异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        // 接收到response, 给channel设计别名，让sendRequest里读取response
        AttributeKey<RpcResponse> RESPONSE_KEY = AttributeKey.valueOf("RPCResponse");
        // 将响应存入 Channel 属性，以便其他地方可以获取
        ctx.channel().attr(RESPONSE_KEY).set(response);
        // 注意：这里不再关闭通道，以便连接可以复用
        ctx.channel().attr(RESPONSE_KEY).set(response);
    }

    /**
     * 处理通道异常情况
     * @param ctx 通道处理器上下文，用于获取通道信息和进行相关操作
     * @param cause 捕获到的异常对象
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 记录异常日志
        log.error("Channel exception occurred", cause);
        // 关闭通道，释放资源
        ctx.close();
    }
}
