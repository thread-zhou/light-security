package com.lightsecurity.core.web;

import com.lightsecurity.core.exception.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AuthenticationEntryPoint {

    void commence(HttpServletRequest request, HttpServletResponse response,
                  AuthenticationException authException) throws IOException, ServletException;

}
