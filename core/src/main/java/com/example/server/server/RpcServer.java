package com.example.server.server;


/**
 * @InterfaceName RpcServer
 * @Description 服务端接口
 */

public interface RpcServer {
    void start(int port);

    void stop();
}
