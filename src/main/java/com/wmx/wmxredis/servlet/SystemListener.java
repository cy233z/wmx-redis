package com.wmx.wmxredis.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @author wangMaoXiong
 * <p>
 * 标准 Servlet 监听器，实现 javax.servlet.ServletContextListener 接口，并重写方法
 * ServletContextListener 属于 Servlet 应用启动关闭监听器，监听容器初始化与销毁。常用的监听器还有：
 * ServletRequestListener：HttpServletRequest 对象的创建和销毁监听器
 * HttpSessionListener：HttpSession 数据对象创建和销毁监听器
 * HttpSessionAttributeListener 监听HttpSession中属性变化
 * ServletRequestAttributeListener 监听ServletRequest中属性变化
 */
@WebListener
public class SystemListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("com.wmx.servlet.SystemListener -- 服务器启动.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("com.wmx.servlet.SystemListener -- 服务器关闭.");
    }
}
