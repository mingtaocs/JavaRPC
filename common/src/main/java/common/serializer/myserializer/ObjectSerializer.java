package common.serializer.myserializer;

import java.io.*;

/**
 * @ClassName ObjectSerializer
 * @Description JDK序列化方式
 */
public class ObjectSerializer implements Serializer {
    /**
     * 将对象序列化为字节数组
     * @param obj 需要序列化的对象
     * @return 序列化后的字节数组
     */
    //利用Java io 对象 -》字节数组
    @Override
    public byte[] serialize(Object obj) {
        byte[] bytes=null;
        //创建一个字节数组输出流，用于将数据写入字节数组
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        try {
            //是一个对象输出流，用于将 Java 对象序列化为字节流，并将其连接到bos上
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            //将对象写入输出流
            oos.writeObject(obj);
            //刷新 ObjectOutputStream，确保所有缓冲区中的数据都被写入到底层流中。
            oos.flush();
            //将bos其内部缓冲区中的数据转换为字节数组
            bytes = bos.toByteArray();
            //关闭流，释放资源
            oos.close();
            bos.close();
        } catch (IOException e) {
            //打印异常信息
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 将字节数组反序列化为对象
     * @param bytes 字节数组数据
     * @param messageType 消息类型（本实现中未使用）
     * @return 反序列化后的对象
     */
    //字节数组 -》对象
    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
        //创建一个字节数组输入流，用于从字节数组中读取数据
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            //创建一个对象输入流，用于从字节数组中读取对象
            ObjectInputStream ois = new ObjectInputStream(bis);
            //从输入流中读取对象
            obj = ois.readObject();
            //关闭流，释放资源
            ois.close();
            bis.close();
        } catch (IOException | ClassNotFoundException e) {
            //打印异常信息
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 获取序列化器类型
     * @return 返回0，表示Java原生序列化器
     */
    //0 代表Java 原生序列器
    @Override
    public int getType() {
        return 0;
    }

    /**
     * 返回序列化器的字符串表示
     * @return 返回"JDK"，表示使用JDK序列化
     */
    @Override
    public String toString() {
        return "JDK";
    }
}