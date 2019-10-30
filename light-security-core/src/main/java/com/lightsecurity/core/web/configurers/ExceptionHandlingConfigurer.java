package com.lightsecurity.core.web.configurers;

import com.lightsecurity.core.authentication.DelegatingAuthenticationEntryPoint;
import com.lightsecurity.core.authentication.Http403ForbiddenEntryPoint;
import com.lightsecurity.core.config.annotation.web.HttpSecurityBuilder;
import com.lightsecurity.core.filter.ExceptionTranslationFilter;
import com.lightsecurity.core.util.matcher.RequestMatcher;
import com.lightsecurity.core.web.AuthenticationEntryPoint;
import com.lightsecurity.core.web.access.AccessDeniedHandler;

import java.util.LinkedHashMap;

public class ExceptionHandlingConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractHttpConfigurer<ExceptionHandlingConfigurer<H>, H> {

    private AuthenticationEntryPoint authenticationEntryPoint;

    private AccessDeniedHandler accessDeniedHandler;

    private LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> defaultEntryPointMappings = new LinkedHashMap<>();

    public ExceptionHandlingConfigurer() {
    }

    public ExceptionHandlingConfigurer<H> accessDeniedHandler(
            AccessDeniedHandler accessDeniedHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
        return this;
    }

    public ExceptionHandlingConfigurer<H> authenticationEntryPoint(
            AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        return this;
    }

    public ExceptionHandlingConfigurer<H> defaultAuthenticationEntryPointFor(AuthenticationEntryPoint entryPoint, RequestMatcher preferredMatcher) {
        this.defaultEntryPointMappings.put(preferredMatcher, entryPoint);
        return this;
    }

    AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return this.authenticationEntryPoint;
    }

    AccessDeniedHandler getAccessDeniedHandler() {
        return this.accessDeniedHandler;
    }

    @Override
    public void configure(H http) throws Exception {
        AuthenticationEntryPoint entryPoint = getAuthenticationEntryPoint(http);
        ExceptionTranslationFilter exceptionTranslationFilter = new ExceptionTranslationFilter(entryPoint);
        if (accessDeniedHandler != null) {
            exceptionTranslationFilter.setAccessDeniedHandler(accessDeniedHandler);
        }
        exceptionTranslationFilter = postProcess(exceptionTranslationFilter);
        http.addFilter(exceptionTranslationFilter);
    }

    AuthenticationEntryPoint getAuthenticationEntryPoint(H http) {
        AuthenticationEntryPoint entryPoint = this.authenticationEntryPoint;
        if (entryPoint == null) {
            entryPoint = createDefaultEntryPoint(http);
        }
        return entryPoint;
    }

    private AuthenticationEntryPoint createDefaultEntryPoint(H http) {
        if (defaultEntryPointMappings.isEmpty()) {
            return new Http403ForbiddenEntryPoint();
        }
        if (defaultEntryPointMappings.size() == 1) {
            return defaultEntryPointMappings.values().iterator().next();
        }
        DelegatingAuthenticationEntryPoint entryPoint = new DelegatingAuthenticationEntryPoint(
                defaultEntryPointMappings);
        entryPoint.setDefaultEntryPoint(defaultEntryPointMappings.values().iterator()
                .next());
        return entryPoint;
    }
}
