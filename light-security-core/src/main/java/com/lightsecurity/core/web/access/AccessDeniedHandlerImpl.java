package com.lightsecurity.core.web.access;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lightsecurity.core.exception.AccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    protected static final Logger logger = LoggerFactory.getLogger(AccessDeniedHandlerImpl.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        //检查服务端是否已经将数据发送到客户端
        if (!response.isCommitted()){
            response.getWriter().write(new ObjectMapper().writeValueAsString(accessDeniedException.getMessage()));
        }
    }
}
