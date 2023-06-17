package com.wmx.wmxredis.exceotion;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.wmx.wmxredis.resultAPI.ResultCode;
import com.wmx.wmxredis.resultAPI.ResultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

/**
 * 系统全局统一异常处理
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2021/12/28 15:03
 */
@RestControllerAdvice
public class CommonExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(CommonExceptionHandler.class);

    /**
     * 1、@Validated 对 RequestBody DTO 参数校验未通过时会抛出 MethodArgumentNotValidException 异常。
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    public ResultData<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder sb = new StringBuilder();
        //遍历校验未通过的字段与错误信息.
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getField()).append("：").append(fieldError.getDefaultMessage()).append(", ");
        }
        if (sb.length() > 0) {
            sb.delete(sb.lastIndexOf(","), sb.length());
        } else {
            sb.append(ex.getMessage());
        }
        //封装后的效果如：password：长度需要在6和18之间, userName：不能为null
        String msg = sb.toString();
        return new ResultData<>(ResultCode.PARAM_IS_FAIL.getCode(), msg, null);
    }

    /**
     * 1、@Validated 对 RequestParam、PathVariable 传参校验未通过时，会抛出 ConstraintViolationException 异常。
     * 2、请求体参数 @RequestBody 如果不是对 DTO 对象属性的校验，则未通过时同样走的 ConstraintViolationException 异常。
     * 比如 @RequestBody @NotEmpty List<Map<String, Object>> dataList
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.OK)
    public ResultData<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error(ex.getMessage(), ex);
        return new ResultData<>(ResultCode.PARAM_IS_FAIL.getCode(), ex.getMessage(), null);
    }

    /**
     * 对没有具体捕获的异常统一捕获处理
     *
     * @param ex
     * @return
     */
    @ExceptionHandler()
    @ResponseStatus(HttpStatus.OK)
    public ResultData<Object> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        // 堆栈转为完整字符串(默认3k个字符长度)
        String stacktraceToString = ExceptionUtil.stacktraceToString(ex);
        return new ResultData<>(ResultCode.FAIL, null, stacktraceToString);
    }

}
