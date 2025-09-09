package common.message;

import lombok.AllArgsConstructor;

/**
 * 消息类型枚举类
 * 使用@AllArgsConstructor注解自动生成全参数构造方法
 * 包含REQUEST和RESPONSE两种消息类型
 */
@AllArgsConstructor
public enum MessageType {
    REQUEST(0), RESPONSE(1);
    private int code;

    public int getCode() {
        return code;
    }
}
