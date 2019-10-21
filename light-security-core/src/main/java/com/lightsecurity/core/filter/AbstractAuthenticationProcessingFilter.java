package com.lightsecurity.core.filter;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.authentication.AuthenticationManager;
import com.lightsecurity.core.context.SecurityContextHolder;
import com.lightsecurity.core.exception.AuthenticationException;
import com.lightsecurity.core.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractAuthenticationProcessingFilter extends GenericFilter{

    private AuthenticationManager authenticationManager;

    protected AbstractAuthenticationProcessingFilter(String defaultFilterProcessUrl){
        setFilterProcessUrl(defaultFilterProcessUrl);
    }

    protected AbstractAuthenticationProcessingFilter(RequestMatcher requestMatcher){
        Assert.notNull(requestMatcher, "requestMatch cannot be null");
        setRequestMatcher(requestMatcher);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }

    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response){
        return getRequestMatcher().matches(request);
    }

    /**
     * 真正的认证方法
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    public abstract Authentication processAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException;

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException{

    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException, ServletException{
        SecurityContextHolder.clearContext();
    }

    public void setFilterProcessUrl(String filterProcessUrl){
        setFilterProcessUrl(filterProcessUrl);
    }

    public void setRequestMatcher(RequestMatcher requestMatcher){
        setRequestMatcher(requestMatcher);
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}
