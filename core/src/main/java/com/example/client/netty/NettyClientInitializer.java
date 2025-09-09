package com.example.client.netty;


import common.serializer.mycoder.MyDecoder;
import common.serializer.mycoder.MyEncoder;
import common.serializer.myserializer.Serializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName NettyClientInitializer
 * @Description 配置自定义的编码器以及Handler
 */
@Slf4j
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {


    /**
     * 初始化SocketChannel的管道，添加必要的编码器、解码器和处理器
     * @param ch SocketChannel实例，用于配置网络通道
     * @throws Exception 如果初始化过程中发生错误
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 获取SocketChannel的管道，用于添加处理器
        ChannelPipeline pipeline = ch.pipeline();

        // 使用自定义的编码器和解码器
        try {
            // 根据传入的序列化器类型初始化编码器
            // 这里使用固定代码3获取序列化器，可能需要根据实际需求调整
            pipeline.addLast(new MyEncoder(Serializer.getSerializerByCode(3)));
            // 添加自定义解码器
            pipeline.addLast(new MyDecoder());
            // 添加客户端处理器，处理具体的业务逻辑
            pipeline.addLast(new NettyClientHandler());

            // 记录管道初始化成功的日志，包含使用的序列化器类型
            log.info("Netty client pipeline initialized with serializer type: {}",Serializer.getSerializerByCode(3).toString());
        } catch (Exception e) {
            // 记录初始化失败的错误日志
            log.error("Error initializing Netty client pipeline", e);
            throw e;  // 重新抛出异常，确保管道初始化失败时处理正确
        }
    }
}
