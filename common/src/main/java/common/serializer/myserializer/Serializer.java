package common.serializer.myserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @InterfaceName Serializer
 * @Description 序列化接口，定义了序列化和反序列化的基本方法

 */
public interface Serializer {
    /**
     * 将对象序列化为字节数组
     * @param obj 需要序列化的对象
     * @return 序列化后的字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 将字节数组反序列化为对象
     * @param bytes 需要反序列化的字节数组
     * @param messageType 消息类型，用于确定反序列化后的对象类型
     * @return 反序列化后的对象
     */
    Object deserialize(byte[] bytes, int messageType);

    /**
     * 获取序列化器的类型标识
     * @return 序列化器的类型标识码
     */
    int getType();

    // 定义静态常量 serializerMap，用于存储不同类型的序列化器
    static final Map<Integer, Serializer> serializerMap = new HashMap<>();

    // 使用 Map 存储序列化器
    static Serializer getSerializerByCode(int code) {
        // 静态映射，保证只初始化一次
        if(serializerMap.isEmpty()) {
            serializerMap.put(0, new ObjectSerializer());
            serializerMap.put(1, new JsonSerializer());
            serializerMap.put(2, new KryoSerializer());
            serializerMap.put(3, new HessianSerializer());
            serializerMap.put(4, new ProtostuffSerializer());
        }
        return serializerMap.get(code); // 如果不存在，则返回 null
    }

    /**
     * 根据序列化器名称获取对应的代码
     * @param serializerName 序列化器名称
     * @return 序列化器代码
     */
    static int getSerializerCodeByName(String serializerName) {
        if(serializerMap.isEmpty()) {
            serializerMap.put(0, new ObjectSerializer());
            serializerMap.put(1, new JsonSerializer());
            serializerMap.put(2, new KryoSerializer());
            serializerMap.put(3, new HessianSerializer());
            serializerMap.put(4, new ProtostuffSerializer());
        }

        for (Map.Entry<Integer, Serializer> entry : serializerMap.entrySet()) {
            if (entry.getValue().toString().equals(serializerName)) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Unknown serializer: " + serializerName);
    }

}
