package com.example.test.balance;

import com.example.client.servicecenter.balance.impl.RandomLoadBalance;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @ClassName RandomLoadBalanceTest
 * @Description 随机负载均衡器测试类，用于测试随机负载均衡器的各种功能
 */
public class RandomLoadBalanceTest {

    private RandomLoadBalance loadBalance; // 声明一个RandomLoadBalance类型的成员变量，用于测试

    /**
     * 在每个测试方法执行前初始化负载均衡器实例
     */
    @Before
    public void setUp() {
        // 在每个测试前初始化负载均衡器
        loadBalance = new RandomLoadBalance();
    }

    /**
     * 测试非空列表情况下的负载均衡功能
     * 验证能够从服务器列表中正确选择一个服务器
     */
    @Test
    public void testBalance_WithNonEmptyList() {
        // 准备一个非空的地址列表
        List<String> addressList = Arrays.asList("server1", "server2", "server3");

        // 使用 balance 方法选择一个服务器
        String selectedServer = loadBalance.balance(addressList);

        // 确保选择的服务器在列表中
        assertTrue(addressList.contains(selectedServer));
    }

    /**
     * 测试传入空列表时的异常处理
     * 预期会抛出IllegalArgumentException异常
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBalance_WithEmptyList() {
        // 测试空的节点列表，应该抛出 IllegalArgumentException 异常
        loadBalance.balance(Arrays.asList());
    }

    /**
     * 测试传入null列表时的异常处理
     * 预期会抛出IllegalArgumentException异常
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBalance_WithNullList() {
        // 测试 null 的节点列表，应该抛出 IllegalArgumentException 异常
        loadBalance.balance(null);
    }

    /**
     * 测试添加节点的功能
     * 验证能够成功添加新节点到负载均衡器
     */
    @Test
    public void testAddNode() {
        // 测试添加节点到负载均衡器
        loadBalance.addNode("server4");

        // 确保新添加的节点在负载均衡器中
        List<String> addressList = Arrays.asList("server1", "server2", "server3", "server4");
        String selectedServer = loadBalance.balance(addressList);
        assertTrue(addressList.contains(selectedServer));
    }

    /**
     * 测试删除节点的功能
     * 验证能够成功从负载均衡器中移除指定节点
     */
    @Test
    public void testDelNode() {
        // 测试从负载均衡器中移除节点
        loadBalance.addNode("server4");
        loadBalance.delNode("server4");

        // 确保删除后的节点不再在负载均衡器中
        List<String> addressList = Arrays.asList("server1", "server2", "server3");
        String selectedServer = loadBalance.balance(addressList);
        assertFalse(addressList.contains("server4"));
    }
}
