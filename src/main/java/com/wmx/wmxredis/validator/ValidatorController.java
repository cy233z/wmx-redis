package com.wmx.wmxredis.validator;

import com.wmx.wmxredis.resultAPI.ResultData;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;


/**
 * Spring Boot + hibernate-validator 实现各种 web 参数校验
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2021/12/28 11:20
 */
@RestController
@RequestMapping("validator")
@Validated
public class ValidatorController {

    /**
     * 编程式校验，此时可以不使用 @Validated 注解。
     * 1、从 Spring 容器中获取 Validator 实例(默认已经有了，直接取值即可)，然后手动校验。
     */
    @Autowired
    private javax.validation.Validator validator;

    /**
     * http://localhost:8080/validator/requestBody/save
     * <p>
     * {"userName":"张三是34","password":"1234546","birthday":"1993-10-25","salary":887,"userCardDTO":{"userCardNum":"TTYY778","publishTime":"2021-12-12"}}
     * <p>
     * 1、RequestBody DTO 参数校验时，@Validated 必须标注在入参前，类上面是不生效的
     * 2、Validated 注解的 value 属性是一个 Class 数组，用于指定校验分组。
     * 3、Validated 指定了校验的分组时，只会对目标分组的属性校验，标记了约束注解，但是未分组的 DTO 属性也不会校验。
     * 4、Validated 未指定校验的分组时，则只会对 DTO 属性上的未分组约束注解进行校验。
     *
     * @param userDTO
     * @return
     */
    @PostMapping("/requestBody/save")
    public ResultData<UserDTO> saveUser(@RequestBody @Validated({UserDTO.Save.class}) UserDTO userDTO) {
        // 校验通过，才会执行业务逻辑处理
        System.out.println("userDTO=" + userDTO);
        return new ResultData<>(userDTO);
    }

    /**
     * http://localhost:8080/validator/requestBody/save2
     * <p>
     * 编程式校验，不使用 @Validated 注解。
     * Set<ConstraintViolation<T>> validate(T object, Class<?>... groups)
     * * 1、验证 object 对象上的所有约束，groups 是用于验证的分组，如果 POJO 对象属性的约束注解进行了分组，则这里也得指定分组。
     * * 2、返回约束冲突，没有时返回空集合。
     * * 3、object、groups 都不允许为  null，否则异常。object 可以是集合
     * * 4、如果在验证过程中发生不可恢复的错误，则引发ValidationException
     * <p>
     * Set<ConstraintViolation<T>> validateProperty(T object,String propertyName,Class<?>... groups)
     * * 1、校验 object 对象上的某个指定属性，其余的不进行校验
     * * 2、被校验的属性值是 object 对象上的
     * <p>
     * Set<ConstraintViolation<T>> validateValue(Class<T> beanType,String propertyName,Object value,Class<?>... groups)
     * * 1、beanType 是 POJO 对象类型，propertyName 是对象的属性名称，groups 是校验分组
     * * 2、value 是被校验的属性值
     *
     * @param userDTO
     * @return
     */
    @PostMapping("/requestBody/save2")
    public ResultData<UserDTO> saveUser2(@RequestBody UserDTO userDTO) {
        Set<ConstraintViolation<UserDTO>> constraintViolationSet = validator.validate(userDTO, UserDTO.Save.class);
        if (constraintViolationSet.isEmpty()) {
            System.out.println("===校验通过：");
        } else {
            System.out.println("===校验失败：");
            //ConstraintViolationImpl{interpolatedMessage='不能为null', propertyPath=userName, rootBeanClass=class com.wmx.validator.UserDTO, messageTemplate='{javax.validation.constraints.NotNull.message}'}
            constraintViolationSet.stream().forEach(item -> System.out.println("\t" + item));
        }

        Set<ConstraintViolation<UserDTO>> validateProperty = validator.validateProperty(userDTO, "password", UserDTO.Save.class);
        Set<ConstraintViolation<UserDTO>> validateValue = validator.validateValue(UserDTO.class, "password", "4545", UserDTO.Save.class);

        System.out.println("validateProperty=" + validateProperty);
        System.out.println("validateValue=" + validateValue);
        return new ResultData<>(userDTO);
    }

    /**
     * http://localhost:8080/validator/requestBody/update
     * <p>
     * {"userName":"张三是34","password":"1234546","uid":"98","birthday":"1993-10-25","salary":887,"userCardDTO":{"userCardNum":"TTYY778","publishTime":"2021-12-12"}}
     *
     * @param userDTO
     * @return
     */
    @PostMapping("/requestBody/update")
    public ResultData<UserDTO> updateUser(@RequestBody @Validated({UserDTO.Update.class}) UserDTO userDTO) {
        // 校验通过，才会执行业务逻辑处理
        System.out.println("userDTO=" + userDTO);
        return new ResultData<>(userDTO);
    }

    /**
     * http://localhost:8080/validator/requestBody/saveList
     * <p>
     * [{"userName":"张三1212","password":"1234546","birthday":"1993-10-25","salary":887,"mobileNumber":"18675456425","userCardDTO":{"userCardNum":"TTYY778","publishTime":"2021-12-12"}},{"userName":"李四1212","password":"a1234546","birthday":"1995-10-25","salary":888,"mobileNumber":"18375456425","userCardDTO":{"userCardNum":"ASYY778","publishTime":"2020-12-12"}}]
     *
     * @param userDTOS
     * @return
     */
    @PostMapping("/requestBody/saveList")
    public ResultData<List<UserDTO>> saveList(@RequestBody @Validated({UserDTO.Save.class}) ValidList<UserDTO> userDTOS) {
        // 校验通过，才会执行业务逻辑处理
        System.out.println(userDTOS);
        return new ResultData<>(userDTOS);
    }

    /**
     * http://localhost:8080/validator/requestBody/saveList2
     *
     * @param userDTOS
     * @return
     */
    @PostMapping("/requestBody/saveList2")
    public ResultData<List<UserDTO>> saveList2(@RequestBody ValidList<UserDTO> userDTOS) {
        Set<ConstraintViolation<ValidList<UserDTO>>> constraintViolations = validator.validate(userDTOS, UserDTO.Save.class);
        if (constraintViolations.isEmpty()) {
            System.out.println("===校验通过");
        } else {
            System.out.println("===校验失败");
            //ConstraintViolationImpl{interpolatedMessage='不能为null', propertyPath=userName, rootBeanClass=class com.wmx.validator.UserDTO, messageTemplate='{javax.validation.constraints.NotNull.message}'}
            constraintViolations.stream().forEach(item -> System.out.println("\t" + item));
        }
        return new ResultData<>(userDTOS);
    }

    /**
     * http://localhost:8080/validator/pathVariable/getById/1
     * <p>
     * 1、@Length：验证字符串长度是否在[mix,max]之间，默认最小值为 0，最大值为 Integer.MAX_VALUE;
     * 2、RequestParam、PathVariable 参数校验时，@Validated 标注在类上，入参前面标记具体的约束。
     *
     * @param uid
     * @return
     */
    @GetMapping("/pathVariable/getById/{uid}")
    public ResultData<String> getById(@PathVariable(value = "uid") @Length(min = 2) String uid) {
        // 校验通过，才会执行业务逻辑处理
        System.out.println("uid=" + uid);
        return new ResultData<>(uid);
    }

    /**
     * http://localhost:8080/validator/requestParam/getByUserName?userName=8989
     * <p>
     * 1、@NotBlank：元素不能是 null，并且必须至少包含一个非空白字符。
     *
     * @param userName
     * @return
     */
    @GetMapping("/requestParam/getByUserName")
    public ResultData<String> getByUserName(@RequestParam @NotBlank String userName) {
        // 校验通过，才会执行业务逻辑处理
        System.out.println("userName=" + userName);
        return new ResultData<>(userName);
    }


}
