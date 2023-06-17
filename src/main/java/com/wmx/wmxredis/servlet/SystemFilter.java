package com.wmx.wmxredis.servlet;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 标准 Servlet 过滤器，实现 javax.servlet.Filter 接口，并重现它的 3 个方法
 * filterName：表示过滤器名称，可以不写
 * value：配置请求过滤的规则，如 "/*" 表示过滤所有请求，包括静态资源，如 "/user/*" 表示 /user 开头的所有请求
 *
 * @author wangMaoXiong
 */
@WebFilter(filterName = "SystemFilter", value = {"/*"})
public class SystemFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("com.wmx.servlet.SystemFilter -- 系统启动...");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //转为 HttpServletRequest 输出请求路径
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        System.out.println("com.wmx.servlet.SystemFilter -- 过滤器放行前...." + request.getRequestURL());
        filterChain.doFilter(servletRequest, servletResponse);
        System.out.println("com.wmx.servlet.SystemFilter -- 过滤器返回后...." + request.getRequestURL());
    }

    @Override
    public void destroy() {
        System.out.println("com.wmx.servlet.SystemFilter -- 系统关闭...");
    }
}
