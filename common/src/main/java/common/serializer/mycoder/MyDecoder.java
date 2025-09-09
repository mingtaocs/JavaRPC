package common.serializer.mycoder;


import common.exception.SerializeException;
import common.message.MessageType;
import common.serializer.myserializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName MyDecoder
 * @Description 解码器
 */
@Slf4j
public class MyDecoder extends ByteToMessageDecoder {
    /**
     * 解码方法，将ByteBuf转换为对象列表
     * @param channelHandlerContext 通道处理器上下文
     * @param in 输入的ByteBuf
     * @param out 解码后的对象列表
     * @throws Exception 可能抛出的异常
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        //检查可读字节数是否足够解析基本头部信息（消息类型+序列化类型+长度）
        if (in.readableBytes() < 8) {  // messageType + serializerType + length
            return;
        }
        //1.读取消息类型（2字节）
        short messageType = in.readShort();
        // 现在还只支持request与response请求
        if (messageType != MessageType.REQUEST.getCode() &&
                messageType != MessageType.RESPONSE.getCode()) {
            log.warn("暂不支持此种数据, messageType: {}", messageType);
            return;
        }
        //2.读取序列化的方式&类型(2字节)
        short serializerType = in.readShort();
        Serializer serializer = Serializer.getSerializerByCode(serializerType);
        if (serializer == null) {
            log.error("不存在对应的序列化器, serializerType: {}", serializerType);
            throw new SerializeException("不存在对应的序列化器, serializerType: " + serializerType);
        }
        //3.读取序列化数组长度(4字节)
        int length = in.readInt();
        if (in.readableBytes() < length) {
            return;  // 数据不完整，等待更多数据
        }
        //4.读取序列化数组
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        log.debug("Received bytes: {}", Arrays.toString(bytes));
        Object deserialize = serializer.deserialize(bytes, messageType);

        out.add(deserialize);
    }
}
