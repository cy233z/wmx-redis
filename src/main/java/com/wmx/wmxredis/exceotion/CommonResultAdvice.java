package com.wmx.wmxredis.exceotion;

import com.wmx.wmxredis.resultAPI.ResultCode;
import com.wmx.wmxredis.resultAPI.ResultData;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Spring MVC 切面 ResponseBodyAdvice 对返回值增强
 * 1、ResponseBodyAdvice 需要绑定到 {@link @RestControllerAdvice} 或者 {@link @ControllerAdvice} 才能生效。
 * 2、注意仅对返回值为 ResponseEntity 或者是有 @ResponseBody 注解的控制器方法进行拦截，
 * * @RestController 标记的类，相当于是类中的所有方法上都加了 @ResponseBody。
 * 3、@RestControllerAdvice 默认是针对所有的控制器，但也可以指定某个包，及其子包都会进行拦截。
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2022/4/16 14:31
 */
@RestControllerAdvice(basePackages = "com.wmx")
public class CommonResultAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 1、当且仅当本方法返回 true 时，下面的 beforeBodyWrite 方法才会执行。
     * 2、可以直接返回 true，此时全部经过下面的 beforeBodyWrite 方法。如下演示的是自己包下面的控制器方法返回时才进行增强。
     *
     * @param returnType：返回类型
     * @param converterType：转换器
     * @return
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        /**
         * Class<?> getDeclaringClass() ：返回声明基础方法或构造函数的类，比如 com.wmx.wmxredis.properties.PropertiesController
         */
        return returnType.getDeclaringClass().getName().contains("com.wmx");
    }

    /**
     * 对返回的数据统一组装成 {@link ResultData} 格式
     *
     * @param body：响应对象(response)中的响应体
     * @param returnType：控制器方法的返回类型
     * @param selectedContentType：通过内容协商选择的内容类型
     * @param selectedConverterType：选择写入响应的转换器类型
     * @param request：当前请求
     * @param response：当前响应
     * @return ：返回传入的主体或修改过的(可能是新的)主体
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        return body instanceof ResultData ? body : new ResultData(ResultCode.SUCCESS, body);
    }
}
