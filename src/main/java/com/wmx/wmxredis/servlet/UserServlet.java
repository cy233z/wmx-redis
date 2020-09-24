package com.wmx.wmxredis.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wangMaoXiong
 * <p>
 * 标准的 Servlet ，实现 javax.servlet.http.HttpServlet. 重写其 doGet 、doPost 方法
 * name :表示 servlet 名称，可以不写，默认为空
 * urlPatterns: 表示请求的路径，如 http://ip:port/context-path/userServlet
 */
@WebServlet(name = "UserServlet", urlPatterns = {"/userServlet"})
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuffer requestURL = req.getRequestURL();
        System.out.println("com.wmx.servlet.UserServlet -- " + requestURL);
        //浏览器重定向到服务器下的 index.html 页面
        resp.sendRedirect("https://gitee.com/wangmx1993/wmx-redis");
    }
}
