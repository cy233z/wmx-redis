package com.wmx.wmxredis.resultAPI;

import java.io.Serializable;

/**
 * Java 设计 API 接口，实现统一格式返回数据
 *
 * @author wangmaoxiong
 * @version 1.0
 * @date 2020/9/30 16:29
 * <p>
 * 页面返回值对象，用于封装返回数据。
 * 1、ResultData 对象中的属性需要提供 setter、getter 方法，控制层的 @ResponseBody 注解会自动将 ResultData 对象转为 json 格式数据返回给页面
 * 2、数据对象使用泛型，方便传输任意类型的数据
 */
public class ResultData<T> implements Serializable {
    private static final long serialVersionUID = 2260434901667977303L;
    /**
     * code：状态码
     * message：消息
     * total：分页时，表示所有页加起来的数据总数
     * pageNum：分页时，表示当前的页码
     * pageSize：分页时，表示每页显示的数据条数
     * pages：总页数
     */
    private Integer code;
    private String message;
    private long total;
    private long pageNum;
    private long pageSize;
    private long pages;
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

    public ResultData(ResultCode resultCode, T data, long total, long pageNum, long pageSize) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        if (pageSize > 0) {
            this.pages = (total % pageSize > 0) ? (total / pageSize + 1) : total / pageSize;
        }
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

    public ResultData(Integer code, String message, T data, long total, long pageNum, long pageSize) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        if (pageSize > 0) {
            this.pages = (total % pageSize > 0) ? (total / pageSize + 1) : total / pageSize;
        }
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

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }


    public long getPageNum() {
        return pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }
}
