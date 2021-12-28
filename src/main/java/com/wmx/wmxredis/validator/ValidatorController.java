package com.wmx.wmxredis.validator;

import com.wmx.wmxredis.resultAPI.ResultData;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;


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
     * http://localhost:8080/validator/requestBody/save
     * <p>
     * 1、RequestBody DTO 参数校验时，@Validated 必须标注在入参前，类上面是不生效的
     *
     * @param userDTO
     * @return
     */
    @PostMapping("/requestBody/save")
    public ResultData<UserDTO> saveUser(@RequestBody @Validated UserDTO userDTO) {
        // 校验通过，才会执行业务逻辑处理
        System.out.println("userDTO=" + userDTO);
        return new ResultData<>(userDTO);
    }

    /**
     * http://localhost:8080/validator/pathVariable/getById
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
