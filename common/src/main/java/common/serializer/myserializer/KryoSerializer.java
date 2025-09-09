package common.serializer.myserializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.example.pojo.User;
import common.exception.SerializeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


/**
 * @ClassName KryoSerializer
 * @Description kryo序列化
 */

public class KryoSerializer implements Serializer {
    // Kryo实例，用于序列化和反序列化操作
    private Kryo kryo;

    /**
     * 构造函数，初始化Kryo实例
     */
    public KryoSerializer() {
        this.kryo = new Kryo();
    }

    /**
     * 序列化方法，将对象转换为字节数组
     * @param obj 需要序列化的对象
     * @return 序列化后的字节数组
     * @throws IllegalArgumentException 如果对象为null
     * @throws SerializeException 如果序列化过程失败
     */
    @Override
    public byte[] serialize(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Cannot serialize null object");
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {

            kryo.writeObject(output, obj); // 使用 Kryo 写入对象
            return output.toBytes(); // 返回字节数组

        } catch (Exception e) {
            throw new SerializeException("Serialization failed");
        }
    }

    /**
     * 反序列化方法，将字节数组转换为对象
     * @param bytes 需要反序列化的字节数组
     * @param messageType 消息类型，用于确定反序列化的目标类
     * @return 反序列化后的对象
     * @throws IllegalArgumentException 如果字节数组为null或为空
     * @throws SerializeException 如果反序列化过程失败或消息类型未知
     */
    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("Cannot deserialize null or empty byte array");
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {

            // 根据 messageType 来反序列化不同的类
            Class<?> clazz = getClassForMessageType(messageType);
            return kryo.readObject(input, clazz); // 使用 Kryo 反序列化对象

        } catch (Exception e) {
            throw new SerializeException("Deserialization failed");
        }
    }

    /**
     * 获取序列化器类型
     * @return 返回序列化器类型，这里返回2
     */
    @Override
    public int getType() {
        return 2;
    }

    /**
     * 根据消息类型获取对应的类
     * @param messageType 消息类型
     * @return 对应的Class对象
     * @throws SerializeException 如果消息类型未知
     */
    private Class<?> getClassForMessageType(int messageType) {
        if (messageType == 1) {
            return User.class;  // 假设我们在此反序列化成 User 类
        } else {
            throw new SerializeException("Unknown message type: " + messageType);
        }
    }

    /**
     * 返回序列化器的字符串表示
     * @return 返回"Kryo"
     */
    @Override
    public String toString() {
        return "Kryo";
    }
}