package com.example.test.serializer;


import common.exception.SerializeException;
import common.serializer.myserializer.HessianSerializer;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * HessianSerializer测试类
 * 用于测试Hessian序列化和反序列化的功能
 */
public class HessianSerializerTest {

    // 初始化HessianSerializer实例
    private HessianSerializer serializer = new HessianSerializer();

    /**
     * 测试正常情况下的序列化和反序列化
     * 验证序列化和反序列化操作的正确性
     */
    @Test
    public void testSerializeAndDeserialize() {
        // 创建一个测试对象
        String original = "Hello, Hessian!";

        // 序列化
        byte[] serialized = serializer.serialize(original);
        assertNotNull("序列化结果不应为 null", serialized);

        // 反序列化
        Object deserialized = serializer.deserialize(serialized, 3);
        assertNotNull("反序列化结果不应为 null", deserialized);

        // 校验反序列化的结果
        assertEquals("反序列化的对象应该与原对象相同", original, deserialized);
    }

    /**
     * 测试使用无效数据进行反序列化的情况
     * 验证异常处理机制是否正常工作
     */
    @Test
    public void testDeserializeWithInvalidData() {
        byte[] invalidData = new byte[]{1, 2, 3}; // 假数据

        // 测试无效数据反序列化
        try {
            serializer.deserialize(invalidData, 3);
            fail("反序列化时应抛出异常");
        } catch (SerializeException e) {
            assertEquals("Deserialization failed", e.getMessage());
        }
    }
}

