package com.example.client.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Netty连接池管理器，实现连接复用
 */
@Slf4j
public class NettyChannelPool {
    // 连接池缓存，key为服务地址，value为Channel
    private final Map<String, Channel> channelPool = new ConcurrentHashMap<>();
    private final Bootstrap bootstrap;

    public NettyChannelPool() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new com.example.client.netty.NettyClientInitializer());
    }

    /**
     * 获取或创建连接
     */
    public Channel getChannel(InetSocketAddress address) {
        String addressKey = address.toString();

        // 如果连接已存在且活跃，直接返回
        Channel channel = channelPool.get(addressKey);
        if (channel != null && channel.isActive()) {
            return channel;
        }

        // 连接不存在或已断开，创建新连接
        try {
            ChannelFuture future = bootstrap.connect(address).sync();
            channel = future.channel();
            channelPool.put(addressKey, channel);
            log.info("创建新连接: {}", address);
            return channel;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to create channel", e);
        }
    }

    /**
     * 关闭所有连接
     */
    public void close() {
        channelPool.forEach((address, channel) -> {
            if (channel != null) {
                channel.close();
            }
        });
        channelPool.clear();
        bootstrap.config().group().shutdownGracefully();
    }
}
