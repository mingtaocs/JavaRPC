package com.example.server.serviceRegister;


import java.net.InetSocketAddress;

/**
 * @InterfaceName ServiceRegister
 * @Description 服务注册接口
 */

public interface ServiceRegister {
    void register(Class<?> clazz, InetSocketAddress serviceAddress);
}
