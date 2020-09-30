package com.wmx.wmxredis.resultAPI;

import java.io.Serializable;

/**
 * @author wangmaoxiong
 * @version 1.0
 * @date 2020/9/30 16:29
 * <p>
 * 页面返回值对象，用于封装返回数据。
 * 1、ResultData 对象中的属性需要提供 setter、getter 方法，控制层的 @ResponseBody 注解会自动将 ResultData 对象转为 json 格式数据返回给页面
 * 2、数据对象使用泛型，方便传输任意类型的数据
 */
public class ResultData<T> implements Serializable {
    /**
     * code：状态码
     * message：消息
     * total：分页时，表示数据总数
     * page：分页时，表示当前的页码
     * size：分页时，表示每页显示的数据条数
     */
    private Integer code;
    private String message;
    private Integer total;
    private Integer page;
    private Integer size;
    private T data;

    public ResultData() {
    }

    public ResultData(T data) {
        this.code = ResultCode.SUCCESS.getCode();
        this.message = ResultCode.SUCCESS.getMessage();
        this.data = data;
    }

    public ResultData(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public ResultData(ResultCode resultCode, T data, Integer total, Integer page, Integer size) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    /**
     * 方便自定义 code 与消息
     *
     * @param code
     * @param message
     * @param data
     */
    public ResultData(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResultData(Integer code, String message, T data, Integer total, Integer page, Integer size) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
