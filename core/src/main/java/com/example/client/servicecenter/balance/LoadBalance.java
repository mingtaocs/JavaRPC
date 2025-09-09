package com.example.client.servicecenter.balance;


import java.util.List;

/**
 * @InterfaceName LoadBalance
 * @Description 负载均衡接口
 */

public interface LoadBalance {
    String balance(List<String> addressList);

    void addNode(String node);

    void delNode(String node);
}
