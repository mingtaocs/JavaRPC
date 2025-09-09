package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName User
 * @Description User对象
 */
@Builder  // 使用Lombok的@Builder注解，提供构建器模式的实现
@Data     // 使用Lombok的@Data注解，自动生成getter、setter、toString等方法
@NoArgsConstructor // 使用Lombok的无参构造方法注解
@AllArgsConstructor // 使用Lombok的全参构造方法注解
public class User implements Serializable {
    // 客户端和服务端共有的
    private Integer id;        // 用户ID，用于唯一标识用户
    private String userName;   // 用户名，用于用户登录和显示
    private Boolean gender;    // 用户性别，true表示男性，false表示女性
}
