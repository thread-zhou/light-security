package com.lightsecurity.core.web.access;

import com.lightsecurity.core.exception.AccessDeniedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AccessDeniedHandler {

    void handle(HttpServletRequest request, HttpServletResponse response,
                AccessDeniedException accessDeniedException) throws IOException,
            ServletException;

}
