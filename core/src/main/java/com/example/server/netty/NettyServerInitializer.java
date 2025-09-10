package com.example.server.netty;


import com.example.JavaRPCApplication;
import com.example.config.JavaRPCConfig;
import com.example.server.provider.ServiceProvider;
import common.serializer.mycoder.MyDecoder;
import common.serializer.mycoder.MyEncoder;
import common.serializer.myserializer.Serializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * @ClassName NettyServerInitializer
 * @Description 服务端初始化器

 */
@AllArgsConstructor
@Slf4j
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceProvider serviceProvider;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        try {
            // 从配置中获取序列化器类型
            JavaRPCConfig config = JavaRPCApplication.getRpcConfig();
            int serializerCode = Serializer.getSerializerCodeByName(config.getSerializer());

            // 根据配置的序列化器类型初始化编码器
            pipeline.addLast(new MyEncoder(Serializer.getSerializerByCode(serializerCode)));
            pipeline.addLast(new MyDecoder());
            pipeline.addLast(new NettyRpcServerHandler(serviceProvider));

            log.info("Netty server pipeline initialized with serializer type: {}", config.getSerializer());
        } catch (Exception e) {
            log.error("Error initializing Netty server pipeline", e);
            throw e;
        }
    }

}
