package com.wmx.wmxredis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义 Redis 分布式锁注解
 * * 1: @Target: 表示注解使用的目标位置, 常用的有:
 * * * TYPE(类,接口,注解,枚举), FIELD(成员变量), METHOD(方法), PARAMETER(形式参数), CONSTRUCTOR(构造器)
 * * 2: @Retention: 表示注解生命范围, 类似 maven pom.xml 文件的 scope 属性, 可选值有:
 * * * SOURCE(编译器将丢弃注解)
 * * * CLASS(注解将由编译器记录在类文件中，但不需要在运行时由VM保留,这是默认行为),
 * * * RUNTIME(注解将由编译器记录在类文件中，并在运行时由VM保留，因此可以反射地读取它们)
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2022/11/20 16:39
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RedisLock {
    /**
     * 特定参数标识，默认取第 0 个下标，-1 表示取整个参数.
     */
    int lockFiled() default 0;

    /**
     * 释放时间，单位秒(s)，默认 30 s
     */
    long lockTime() default 30;

    /**
     * 超时重试次数，默认 3次.
     */
    int retryCount() default 3;

    /**
     * 描述信息
     *
     * @return
     */
    String desc() default "";
}