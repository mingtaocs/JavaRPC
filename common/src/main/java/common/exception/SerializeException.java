package common.exception;

/**
 * @ClassName SerializeException
 *  自定义序列化异常类，继承自RuntimeException
 * 用于在序列化过程中发生错误时抛出异常
 */
public class SerializeException extends RuntimeException{
    /**
     * 构造函数，创建一个带有错误信息的序列化异常
     * @param message 异常的详细信息
     */
    public SerializeException(String message) {
        super(message);
    }
    /**
     * 构造函数，创建一个带有错误信息和原因的序列化异常
     * @param message 异常的详细信息
     * @param cause 导致此异常的底层异常原因
     */
    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
