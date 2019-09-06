package com.lightsecurity.web.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public abstract class AbstractSecurityWebApplicationInitializer implements WebApplicationInitializer {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        final boolean debug = logger.isDebugEnabled();
        if (debug){
            logger.debug("是的，启动了");
        }
    }
}
