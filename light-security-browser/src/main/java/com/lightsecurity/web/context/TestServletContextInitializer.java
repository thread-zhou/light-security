package com.lightsecurity.web.context;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * spring boot内置tomcat加载ServletContext测试, 不推荐这种方式
 */
@Configuration
public class TestServletContextInitializer implements ServletContextInitializer {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        logger.info("内置tomcat加载了我, ServletContext：{}", servletContext);
    }
}
