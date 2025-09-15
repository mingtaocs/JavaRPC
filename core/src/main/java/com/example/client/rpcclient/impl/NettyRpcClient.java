package com.example.client.rpcclient.impl;

import com.example.client.netty.NettyClientInitializer;
import com.example.client.pool.NettyChannelPool;
import com.example.client.rpcclient.RpcClient;
import common.message.RpcRequest;
import common.message.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @ClassName NettyRpcClient
 * @Description Netty客户端，使用连接池实现连接复用
 */
@Slf4j
public class NettyRpcClient implements RpcClient {
    private static NettyChannelPool channelPool;
    private final InetSocketAddress address;

    public NettyRpcClient(InetSocketAddress serviceAddress) {
        this.address = serviceAddress;
        // 初始化连接池
        if (channelPool == null) {
            channelPool = new NettyChannelPool();
        }
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        if (address == null) {
            log.error("服务发现失败，返回的地址为 null");
            return RpcResponse.fail("服务发现失败，地址为 null");
        }

        Channel channel = null;
        try {
            // 从连接池获取连接
            channel = channelPool.getChannel(address);

            // 发送数据
            channel.writeAndFlush(request);

            // 等待响应
            channel.closeFuture().await();

            // 获取响应
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            RpcResponse response = channel.attr(key).get();
            if (response == null) {
                log.error("服务响应为空，可能是请求失败或超时");
                return RpcResponse.fail("服务响应为空");
            }

            log.info("收到响应: {}", response);
            return response;
        } catch (InterruptedException e) {
            log.error("请求被中断，发送请求失败: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("发送请求时发生异常: {}", e.getMessage(), e);
        }
        // 注意：这里不关闭连接，以便复用
        return RpcResponse.fail("请求失败");
    }

    // 关闭连接池
    @Override
    public void close() {
        if (channelPool != null) {
            channelPool.close();
            channelPool = null;
        }
    }
}
