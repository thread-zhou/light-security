package com.lightsecurity.core.authentication.handler;

import com.lightsecurity.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse re, Authentication authentication) throws IOException, ServletException {
        if (logger.isWarnEnabled()){
            logger.warn("请实现自己的成功处理器");
        }
    }
}
