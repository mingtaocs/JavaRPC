package com.example.consumer;


import com.example.config.JavaRPCConfig;
import common.util.ConfigUtil;

/**
 * @ClassName ConsumerTestConfig
 * @Description 测试配置顶
 */
public class ConsumerTestConfig {
    public static void main(String[] args) {
        JavaRPCConfig rpc = ConfigUtil.loadConfig(JavaRPCConfig.class, "rpc");
        System.out.println(rpc);
    }

}
