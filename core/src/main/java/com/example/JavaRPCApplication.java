package com.example;


import com.example.config.JavaRPCConfig;
import com.example.config.RpcConstant;
import common.util.ConfigUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName RpcApplication
 * @Description 测试配置顶，学习更多参考Dubbo
 */
@Slf4j
public class JavaRPCApplication {
    private static volatile JavaRPCConfig rpcConfigInstance;

    public static void initialize(JavaRPCConfig customRpcConfig) {
        rpcConfigInstance = customRpcConfig;
        log.info("RPC 框架初始化，配置 = {}", customRpcConfig);
    }

    public static void initialize() {
        JavaRPCConfig customRpcConfig;
        try {
            customRpcConfig = ConfigUtil.loadConfig(JavaRPCConfig.class, RpcConstant.CONFIG_FILE_PREFIX);
            log.info("成功加载配置文件，配置文件名称 = {}", RpcConstant.CONFIG_FILE_PREFIX); // 添加成功加载的日志
        } catch (Exception e) {
            // 配置加载失败，使用默认配置
            customRpcConfig = new JavaRPCConfig();
            log.warn("配置加载失败，使用默认配置");
        }
        initialize(customRpcConfig);
    }

    public static JavaRPCConfig getRpcConfig() {
        if (rpcConfigInstance == null) {
            synchronized (JavaRPCApplication.class) {
                if (rpcConfigInstance == null) {
                    initialize();  // 确保在第一次调用时初始化
                }
            }
        }
        return rpcConfigInstance;
    }
}
