package com.example.client.servicecenter;

import com.example.client.cache.ServiceCache;
import com.example.client.servicecenter.ZKWatcher.watchZK;
import com.example.client.servicecenter.balance.LoadBalance;
import com.example.client.servicecenter.balance.impl.ConsistencyHashBalance;
import common.message.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class ZKServiceCenter implements ServiceCenter {
    private static final String ROOT_PATH = "MyRPC";
    private static final String RETRY = "CanRetry";
    private final CuratorFramework client;
    private final ServiceCache cache;
    private final LoadBalance loadBalance;
    private final Set<String> retryServiceCache = new CopyOnWriteArraySet<>();

    public ZKServiceCenter() throws InterruptedException {
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000)
                .retryPolicy(policy)
                .namespace(ROOT_PATH)
                .build();
        this.client.start();
        log.info("Zookeeper 连接成功");

        this.cache = new ServiceCache();
        this.loadBalance = new ConsistencyHashBalance();

        watchZK watcher = new watchZK(client, cache, loadBalance);
        watcher.watchToUpdate(ROOT_PATH);
    }

    @Override
    public InetSocketAddress serviceDiscovery(RpcRequest request) {
        String serviceName = request.getInterfaceName();
        try {
            List<String> addressList = cache.getServiceListFromCache(serviceName);

            if (addressList == null) {
                addressList = client.getChildren().forPath("/" + serviceName);
                if (addressList.isEmpty()) {
                    log.warn("未找到服务：{}", serviceName);
                    return null;
                }
                updateLoadBalance(serviceName, addressList);
            }

            String address = loadBalance.balance(addressList);
            return parseAddress(address);
        } catch (Exception e) {
            log.error("服务发现失败，服务名：{}", serviceName, e);
        }
        return null;
    }

    private void updateLoadBalance(String serviceName, List<String> newAddressList) {
        List<String> currentAddressList = cache.getServiceListFromCache(serviceName);

        if (currentAddressList == null || currentAddressList.isEmpty()) {
            for (String address : newAddressList) {
                loadBalance.addNode(address);
            }
            return;
        }

        for (String newAddress : newAddressList) {
            if (!currentAddressList.contains(newAddress)) {
                loadBalance.addNode(newAddress);
            }
        }

        for (String currentAddress : currentAddressList) {
            if (!newAddressList.contains(currentAddress)) {
                loadBalance.delNode(currentAddress);
            }
        }
    }

    @Override
    public boolean checkRetry(InetSocketAddress serviceAddress, String methodSignature) {
        if (retryServiceCache.isEmpty()) {
            try (CuratorFramework rootClient = client.usingNamespace(RETRY)) {
                List<String> retryableMethods = rootClient.getChildren()
                        .forPath("/" + getServiceAddress(serviceAddress));
                retryServiceCache.addAll(retryableMethods);
            } catch (Exception e) {
                log.error("检查重试失败，方法签名：{}", methodSignature, e);
            }
        }
        return retryServiceCache.contains(methodSignature);
    }

    public void handleServiceFault(String serviceName, String faultAddress) {
        try {
            cache.delete(serviceName, faultAddress);
            loadBalance.delNode(faultAddress);
            log.info("服务节点故障处理完成：服务名 {}，故障节点 {}", serviceName, faultAddress);
        } catch (Exception e) {
            log.error("处理服务节点故障失败：服务名 {}，故障节点 {}", serviceName, faultAddress, e);
        }
    }

    @Override
    public void close() {
        client.close();
    }

    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() + ":" + serverAddress.getPort();
    }

    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }
}
