package com.example.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解：Retryable
 * 该注解用于标记需要进行重试的方法
 *
 * @Retention(RetentionPolicy.RUNTIME) 表示该注解会在运行时保留
 * @Target(ElementType.METHOD) 表示该注解只能用于方法上
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Retryable {

}

