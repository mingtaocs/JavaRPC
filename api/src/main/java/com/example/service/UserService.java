package com.example.service;


import com.example.annotation.Retryable;
import com.example.pojo.User;

/**
 * @InterfaceName UserService
 * @Description 接口
 */

public interface UserService {

    // 查询
    @Retryable
    User getUserByUserId(Integer id);

    // 新增
    @Retryable
    Integer insertUserId(User user);
}
