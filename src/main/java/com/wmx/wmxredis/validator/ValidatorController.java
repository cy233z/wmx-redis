package com.wmx.wmxredis.validator;

import com.wmx.wmxredis.resultAPI.ResultData;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Spring Boot + hibernate-validator 实现各种 web 参数校验
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2021/12/28 11:20
 */
@RestController
@RequestMapping("validator")
public class ValidatorController {

    /**
     * http://localhost:8080/validator/requestBody/save
     *
     * @param userDTO
     * @return
     */
    @PostMapping("/requestBody/save")
    public ResultData<UserDTO> saveUser(@RequestBody @Validated UserDTO userDTO) {
        System.out.println("userDTO=" + userDTO);

        // 校验通过，才会执行业务逻辑处理
        return new ResultData<>(userDTO);
    }

}
