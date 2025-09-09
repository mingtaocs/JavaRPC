package com.example.client.rpcclient;


import common.message.RpcRequest;
import common.message.RpcResponse;

/**
 * @InterfaceName RpcClient
 * @Description 定义底层通信方法
 */

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
    void close();
}
