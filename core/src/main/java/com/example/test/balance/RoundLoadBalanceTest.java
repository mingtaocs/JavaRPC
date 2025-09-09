package com.example.test.balance;

import com.example.client.servicecenter.balance.impl.RoundLoadBalance;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @ClassName RoundLoadBalanceTest
 * @Description 轮询测试类
 */
public class RoundLoadBalanceTest {

    // 声明一个RoundLoadBalance类型的私有成员变量，用于测试
    private RoundLoadBalance loadBalance;

    /**
     * 初始化方法，在每个测试方法执行前调用
     * 用于设置测试环境，初始化负载均衡器对象
     */
    @Before
    public void setUp() {
        // 在每个测试前初始化负载均衡器
        loadBalance = new RoundLoadBalance();
    }

    /**
     * 测试非空列表情况下的负载均衡方法
     * 验证是否能正确从非空地址列表中选择一个服务器
     */
    @Test
    public void testBalance_WithNonEmptyList() {
        // 准备一个非空的地址列表
        List<String> addressList = Arrays.asList("server1", "server2", "server3");

        // 执行 balance 方法并获取返回的服务器
        String selectedServer = loadBalance.balance(addressList);

        // 确保选择的服务器在列表中
        assertTrue(addressList.contains(selectedServer));
    }

    /**
     * 测试空列表情况下的负载均衡方法
     * 验证当传入空列表时是否会抛出IllegalArgumentException异常
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBalance_WithEmptyList() {
        // 测试空的节点列表，应该抛出 IllegalArgumentException 异常
        loadBalance.balance(Arrays.asList());
    }

    /**
     * 测试null列表情况下的负载均衡方法
     * 验证当传入null时是否会抛出IllegalArgumentException异常
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBalance_WithNullList() {
        // 测试 null 的节点列表，应该抛出 IllegalArgumentException 异常
        loadBalance.balance(null);
    }

    /**
     * 测试添加节点功能
     * 验证能否成功添加新节点到负载均衡器中
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
     * 测试删除节点功能
     * 验证能否成功从负载均衡器中移除指定节点
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

    /**
     * 测试轮询功能
     * 验证负载均衡器是否能按照轮询顺序选择服务器
     */
    @Test
    public void testBalance_RoundRobin() {
        // 测试负载均衡是否按轮询顺序选择服务器
        List<String> addressList = Arrays.asList("server1", "server2", "server3");

        // 轮询选择服务器
        String firstSelection = loadBalance.balance(addressList);
        String secondSelection = loadBalance.balance(addressList);
        String thirdSelection = loadBalance.balance(addressList);
        String fourthSelection = loadBalance.balance(addressList);  // Should loop back to first

        // 确保选择的服务器是轮询顺序的
        assertNotEquals(firstSelection, secondSelection);
        assertNotEquals(secondSelection, thirdSelection);
        assertNotEquals(thirdSelection, fourthSelection);
        assertEquals(firstSelection, fourthSelection);  // Should be back to the first
    }
}
