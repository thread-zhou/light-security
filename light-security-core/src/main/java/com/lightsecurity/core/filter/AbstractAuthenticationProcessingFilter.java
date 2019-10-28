package com.lightsecurity.core.filter;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.LightSecurityMessageSource;
import com.lightsecurity.core.authentication.AuthenticationDetailsSource;
import com.lightsecurity.core.authentication.AuthenticationManager;
import com.lightsecurity.core.authentication.WebAuthenticationDetailsSource;
import com.lightsecurity.core.authentication.handler.AuthenticationFailureHandler;
import com.lightsecurity.core.authentication.handler.AuthenticationSuccessHandler;
import com.lightsecurity.core.authentication.handler.SimpleAppAuthenticationFailureHandler;
import com.lightsecurity.core.authentication.handler.SimpleAppAuthenticationSuccessHandler;
import com.lightsecurity.core.context.SecurityContextHolder;
import com.lightsecurity.core.exception.AuthenticationException;
import com.lightsecurity.core.exception.InternalAuthenticationServiceException;
import com.lightsecurity.core.util.matcher.RequestMatcher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
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
    protected ApplicationEventPublisher eventPublisher;
    protected AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    protected MessageSourceAccessor messages = LightSecurityMessageSource.getAccessor();

    private AuthenticationSuccessHandler successHandler = new SimpleAppAuthenticationSuccessHandler();
    private AuthenticationFailureHandler failureHandler = new SimpleAppAuthenticationFailureHandler();

    protected AbstractAuthenticationProcessingFilter(String defaultFilterProcessUrl){
        setFilterProcessUrl(defaultFilterProcessUrl);
    }

    protected AbstractAuthenticationProcessingFilter(RequestMatcher requestMatcher){
        Assert.notNull(requestMatcher, "requestMatch cannot be null");
        setRequestMatcher(requestMatcher);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!requiresAuthentication(request, response)){
            filterChain.doFilter(request, response);

            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Request is to process authentication");
        }

        Authentication authResult;

        try {
            authResult = processAuthentication(request, response);
            if (authResult == null){
                return;
            }
            //todo
        }catch (InternalAuthenticationServiceException failed){
            logger.error( "An internal error occurred while trying to authenticate the user.", failed);
            unsuccessfulAuthentication(request, response, failed);

            return;
        }catch (AuthenticationException failed){
            unsuccessfulAuthentication(request, response, failed);

            return;
        }

        successfulAuthentication(request, response, filterChain, authResult);

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

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException{
        if (logger.isDebugEnabled()) {
            logger.debug("Authentication success. Updating SecurityContextHolder to contain: "
                    + authResult);
        }
        SecurityContextHolder.getContext().setAuthentication(authResult);

        // TODO: 2019/10/28
//        if (this.eventPublisher != null) {
//            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
//        }

        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException{
        SecurityContextHolder.clearContext();
        if (logger.isDebugEnabled()) {
            logger.debug("Authentication request failed: " + failed.toString(), failed);
            logger.debug("Updated SecurityContextHolder to contain null Authentication");
            logger.debug("Delegating to authentication failure handler " + failureHandler);
        }

        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    public void setFilterProcessUrl(String filterProcessUrl){
        super.setFilterProcessUrl(filterProcessUrl);
    }

    public void setRequestMatcher(RequestMatcher requestMatcher){
        super.setRequestMatcher(requestMatcher);
    }

    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {

        this.eventPublisher = eventPublisher;
    }

    public ApplicationEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        Assert.notNull(authenticationDetailsSource,
                "AuthenticationDetailsSource required");
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}
