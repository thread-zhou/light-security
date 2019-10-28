package com.lightsecurity.core.authentication;

import javax.servlet.http.HttpServletRequest;

public class WebAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {
    @Override
    public WebAuthenticationDetails bindDetails(HttpServletRequest context) {
        return new WebAuthenticationDetails(context);
    }
}
