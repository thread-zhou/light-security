package com.lightsecurity.core.authentication.handler;

import com.lightsecurity.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AuthenticationSuccessHandler {

    void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse re,
                                 Authentication authentication) throws IOException, ServletException;

}
