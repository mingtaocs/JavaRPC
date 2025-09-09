package common.serializer.myserializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import common.message.RpcRequest;
import common.message.RpcResponse;


/**
 * @ClassName JsonSerializer
 * @Description json序列化
 */
public class JsonSerializer implements Serializer {
    /**
     * 序列化方法，将对象转换为字节数组
     * @param obj 需要序列化的对象
     * @return 序列化后的字节数组
     */
    @Override
    public byte[] serialize(Object obj) {
        byte[] bytes = JSONObject.toJSONBytes(obj);
        return bytes;
    }

    /**
     * 反序列化方法，将字节数组转换为指定类型的对象
     * @param bytes 需要反序列化的字节数组
     * @param messageType 消息类型，0表示请求，1表示响应
     * @return 反序列化后的对象
     */
    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
        // 传输的消息分为request与response
        switch (messageType){
            case 0:
                // 将字节数组解析为RpcRequest对象
                RpcRequest request = JSON.parseObject(bytes, RpcRequest.class);
                Object[] objects = new Object[request.getParams().length];
                // 把json字串转化成对应的对象， fastjson可以读出基本数据类型，不用转化
                // 对转换后的request中的params属性逐个进行类型判断
                for(int i = 0; i < objects.length; i++){
                    Class<?> paramsType = request.getParamsType()[i];
                    //判断每个对象类型是否和paramsTypes中的一致
                    if (!paramsType.isAssignableFrom(request.getParams()[i].getClass())){
                        //如果不一致，就行进行类型转换
                        objects[i] = JSONObject.toJavaObject((JSONObject) request.getParams()[i],request.getParamsType()[i]);
                    }else{
                        //如果一致就直接赋给objects[i]
                        objects[i] = request.getParams()[i];
                    }
                }
                request.setParams(objects);
                obj = request;
                break;
            case 1:
                // 将字节数组解析为RpcResponse对象
                RpcResponse response = JSON.parseObject(bytes, RpcResponse.class);
                // 如果类型为空，说明返回错误
                if(response.getDataType()==null){
                    obj = RpcResponse.fail("类型为空");
                    break;
                }
                Class<?> dataType = response.getDataType();
                //判断转化后的response对象中的data的类型是否正确
                if(response.getData() != null && !dataType.isAssignableFrom(response.getData().getClass())){
                    response.setData(JSONObject.toJavaObject((JSONObject) response.getData(),dataType));
                }
                obj = response;
                break;
            default:
                System.out.println("暂时不支持此种消息");
                throw new RuntimeException();
        }
        return obj;
    }

    //1 代表json序列化方式
    /**
     * 获取序列化类型
     * @return 返回序列化类型编号，1代表JSON序列化
     */
    @Override
    public int getType() {
        return 1;
    }

    /**
     * 返回序列化方式的字符串表示
     * @return 返回"Json"字符串
     */
    @Override
    public String toString() {
        return "Json";
    }
}
