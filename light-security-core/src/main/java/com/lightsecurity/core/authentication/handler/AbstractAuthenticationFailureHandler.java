package com.lightsecurity.core.authentication.handler;

import com.lightsecurity.core.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractAuthenticationFailureHandler implements AuthenticationFailureHandler{

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (logger.isWarnEnabled()){
            logger.warn("请实现自己的失败处理器");
        }
    }
}
