package com.example.client.servicecenter.ZKWatcher;

import com.example.client.cache.ServiceCache;
import com.example.client.servicecenter.balance.LoadBalance;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

@Slf4j
public class watchZK {
    private final CuratorFramework client;
    private final ServiceCache cache;
    private final LoadBalance loadBalance;

    public watchZK(CuratorFramework client, ServiceCache cache, LoadBalance loadBalance) {
        this.client = client;
        this.cache = cache;
        this.loadBalance = loadBalance;
    }

    public void watchToUpdate(String path) throws InterruptedException {
        CuratorCache curatorCache = CuratorCache.build(client, "/");
        curatorCache.listenable().addListener(new CuratorCacheListener() {
            @Override
            public void event(Type type, ChildData childData, ChildData childData1) {
                switch (type.name()) {
                    case "NODE_CREATED":
                        handleNodeCreation(childData1);
                        break;
                    case "NODE_CHANGED":
                        handleNodeChange(childData, childData1);
                        break;
                    case "NODE_DELETED":
                        handleNodeDeletion(childData);
                        break;
                }
            }
        });
        curatorCache.start();
    }

    private void handleNodeCreation(ChildData childData) {
        String[] pathList = parsePath(childData);
        if (pathList.length <= 2) return;

        String serviceName = pathList[1];
        String address = pathList[2];
        cache.addServiceToCache(serviceName, address);
        loadBalance.addNode(address);
        log.info("节点创建：服务名称 {} 地址 {}", serviceName, address);
    }

    private void handleNodeChange(ChildData oldChildData, ChildData newChildData) {
        String[] oldPathList = parsePath(oldChildData);
        String[] newPathList = parsePath(newChildData);

        cache.replaceServiceAddress(oldPathList[1], oldPathList[2], newPathList[2]);
        loadBalance.delNode(oldPathList[2]);
        loadBalance.addNode(newPathList[2]);
        log.info("节点更新：服务名称 {} 地址从 {} 更新为 {}",
                oldPathList[1], oldPathList[2], newPathList[2]);
    }

    private void handleNodeDeletion(ChildData childData) {
        String[] pathList = parsePath(childData);
        if (pathList.length <= 2) return;

        String serviceName = pathList[1];
        String address = pathList[2];
        cache.delete(serviceName, address);
        loadBalance.delNode(address);
        log.info("节点删除：服务名称 {} 地址 {}", serviceName, address);
    }

    private String[] parsePath(ChildData childData) {
        return childData.getPath().split("/");
    }
}
