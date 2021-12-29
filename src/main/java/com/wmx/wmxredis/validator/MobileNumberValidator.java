package com.wmx.wmxredis.validator;

import org.hibernate.validator.internal.constraintvalidators.bv.NotBlankValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.URLValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对自定义约束注解 {@link MobileNumber} 进行校验，可以参考{@link NotBlankValidator}、{@link URLValidator}
 * 1、实现 ConstraintValidator<A extends Annotation, T> 接口，A 是自定义的约束注解，T 是校验的数据类型。
 * 2、校验手机号码格式是否正确，接受字符串类型
 * 3、如果为 null 则不校验
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2021/12/29 16:36
 */
public class MobileNumberValidator implements ConstraintValidator<MobileNumber, String> {

    private static final Logger log = LoggerFactory.getLogger(MobileNumberValidator.class);

    private static final Pattern pattern = Pattern.compile("1[3-9]\\d{9}");

    private String dec;

    /**
     * 1、校验初始化方法，先于  isValid 方法执行
     * 2、用于做一些校验之前的初始化操作，比如获取自定义约束注解上的属性值
     *
     * @param mobileNumber
     */
    @Override
    public void initialize(MobileNumber mobileNumber) {
        dec = mobileNumber.dec();
    }

    /**
     * 上面初始化方法执行完成后，进入本方法进行校验，返回 true 表示校验通过，返回 false 表示未通过。
     *
     * @param value
     * @param context
     * @return
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        log.debug(dec);
        //不为 null 时才进行校验
        if (value != null) {
            Matcher matcher = pattern.matcher(value);
            return matcher.matches();
        }
        return true;
    }
}
