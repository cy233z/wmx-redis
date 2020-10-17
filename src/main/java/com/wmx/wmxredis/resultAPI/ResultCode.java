package com.wmx.wmxredis.resultAPI;

/**
 * @author wangMaoXiong
 * @author wangmaoxiong
 * @version 1.0
 * @date 2020/9/30 16:30
 * <p>
 * 页面返回数据状态枚举值
 * 1000～1999 区间表示参数错误
 * 2000～2999 区间表示用户错误
 * 3000～3999 区间表示接口异常
 */
public enum ResultCode {

    /*成功状态码*/
    SUCCESS(200, "success"),
    /*失败状态码*/
    FAIL(500, "fail"),

    /*参数错误:1000-1999*/
    PARAM_IS_INVALID(1001, "参数无效"),

    PARAM_IS_BLANK(1002, "参数为空"),

    PARAM_TYP_EBIND_ERROR(1003, "参数类型错误"),

    PARAM_NOT_C0MPLETE(1004, "参数缺失"),

    /*用户错误:2000-2999*/
    USER_NOT_L0GGED_IN(2001, "用户未登录，访问的路径需要验证，请登录"),

    USER_L0GIN_ERROR(2002, "账号不存在或密码错误"),

    USER_ACCOUNT_F0_RBIDDEN(2003, "账号已被禁用");

    private Integer code;
    private String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }


}


