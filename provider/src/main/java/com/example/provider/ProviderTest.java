package com.example.provider;

import com.example.JavaRPCApplication;
import com.example.provider.impl.UserServiceImpl;
import com.example.server.provider.ServiceProvider;
import com.example.server.server.RpcServer;
import com.example.server.server.impl.NettyRpcServer;
import com.example.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName ProviderExample
 * @Description 测试服务端
 */
@Slf4j
public class ProviderTest {

    public static void main(String[] args) throws InterruptedException {
        JavaRPCApplication.initialize();
        String ip=JavaRPCApplication.getRpcConfig().getHost();
        int port=JavaRPCApplication.getRpcConfig().getPort();
        // 创建 UserService 实例
        UserService userService = new UserServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider(ip, port);
        // 发布服务接口到 ServiceProvider
        serviceProvider.provideServiceInterface(userService);  // 可以设置是否支持重试

        // 启动 RPC 服务器并监听端口
        RpcServer rpcServer = new NettyRpcServer(serviceProvider);
        rpcServer.start(port);  // 启动 Netty RPC 服务，监听 port 端口
        log.info("RPC 服务端启动，监听端口" + port);
    }

}
