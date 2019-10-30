package com.lightsecurity.core.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface RedirectStrategy {

    void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url)
            throws IOException;
}
