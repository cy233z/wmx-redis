package com.wmx.wmxredis.validator;

import org.hibernate.validator.constraints.URL;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自定义 Spring Validation 约束注解，可以参考{@link NotBlank}、{@link URL}
 * 1、校验手机号码格式是否正确，接受字符串类型
 * 2、如果为 null 则不校验
 * 3、Constraint 指向具体的校验类
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2021/12/29 16:28
 */
@Documented
@Constraint(validatedBy = {MobileNumberValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface MobileNumber {

    /**
     * 校验未通过时，默认错误信息
     *
     * @return
     */
    String message() default "手机号码格式不正确";

    /**
     * 注解描述信息
     *
     * @return
     */
    String dec() default "";

    /**
     * 校验分组
     *
     * @return
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     *
     * @return
     */
    Class<? extends Payload>[] payload() default {};

}
