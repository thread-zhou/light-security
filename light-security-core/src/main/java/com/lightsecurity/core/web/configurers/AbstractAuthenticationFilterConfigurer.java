package com.lightsecurity.core.web.configurers;

import com.lightsecurity.core.authentication.AuthenticationDetailsSource;
import com.lightsecurity.core.authentication.AuthenticationManager;
import com.lightsecurity.core.config.annotation.web.HttpSecurityBuilder;
import com.lightsecurity.core.filter.AbstractAuthenticationProcessingFilter;
import com.lightsecurity.core.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractAuthenticationFilterConfigurer<B extends HttpSecurityBuilder<B>, T extends AbstractAuthenticationFilterConfigurer<B, T, F>, F extends AbstractAuthenticationProcessingFilter>
        extends AbstractHttpConfigurer<T, B> {

    private final F authFilter;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    private String loginProcessingUrl;

    private  boolean permitAll;

    protected AbstractAuthenticationFilterConfigurer(F authFilter, String defaultLoginProcessUrl){
        this.authFilter = authFilter;
        if (defaultLoginProcessUrl != null){
            loginProcessingUrl(defaultLoginProcessUrl);
        }
    }

    protected final F getAuthenticationFilter(){
        return authFilter;
    }

    @Override
    public void configure(B http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        authFilter.setAuthenticationManager(authenticationManager);
        //设置失败和成功处理器 todo
        if (authenticationDetailsSource != null){
            authFilter.setAuthenticationDetailsSource(authenticationDetailsSource);
        }
        F filter = postProcess(authFilter);
        http.addFilter(filter);
    }

    protected T loginProcessingUrl(String loginProcessingUrl){
        this.loginProcessingUrl = loginProcessingUrl;
        authFilter.setRequestMatcher(createLoginProcessUrlMatcher(loginProcessingUrl));
        return getSelf();
    }

    public final T permitAll(){
        return permitAll(true);
    }

    public final T permitAll(boolean permitAll){
        this.permitAll = permitAll;
        return getSelf();
    }

    private T getSelf() {
        return (T) this;
    }

    protected abstract RequestMatcher createLoginProcessUrlMatcher(String loginProcessingUrl);
}
