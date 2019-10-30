package com.lightsecurity.core.web.logout;

import com.lightsecurity.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LogoutHandler {

    void logout(HttpServletRequest request, HttpServletResponse response,
                Authentication authentication);

}
