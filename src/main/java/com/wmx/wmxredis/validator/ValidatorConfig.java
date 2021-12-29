package com.wmx.wmxredis.validator;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;


/**
 * 自定义 javax.validation.Validator 配置，覆盖默认值。
 * 1、无论是对 @Validated 注解、还是编程式校验都有效。
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2021/12/29 18:50
 */
@Configuration
public class ValidatorConfig {

    @Bean
    public javax.validation.Validator validator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                // 快速失败模式
                .failFast(true)
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }
}
