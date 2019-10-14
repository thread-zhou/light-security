package com.lightsecurity.core.web.builders;

import com.lightsecurity.core.authority.AuthenticationManagerBuilder;
import com.lightsecurity.core.authority.AuthenticationProvider;
import com.lightsecurity.core.config.annotation.*;
import com.lightsecurity.core.config.annotation.web.HttpSecurityBuilder;
import com.lightsecurity.core.filter.DefaultSecurityFilterChain;
import com.lightsecurity.core.userdetails.UserDetailsService;
import com.lightsecurity.core.util.matcher.AnyRequestMatcher;
import com.lightsecurity.core.util.matcher.RequestMatcher;
import com.lightsecurity.core.web.configurers.CorsConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Filter;

public final class HttpSecurity extends AbstractConfiguredSecurityBuilder<DefaultSecurityFilterChain, HttpSecurity>
        implements SecurityBuilder<DefaultSecurityFilterChain>, HttpSecurityBuilder<HttpSecurity> {

    private List<Filter> filters = new ArrayList<>();

    private RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;

    private FilterComparator comparator = new FilterComparator();


    public HttpSecurity(ObjectPostProcessor<Object> objectPostProcessor,
                        AuthenticationManagerBuilder authenticationBuilder,
                        Map<Class<? extends Object>, Object> sharedObjects) {
        super(objectPostProcessor);
        //todo 完成HttpSecurity的编写
        Assert.notNull(authenticationBuilder, "authenticationBuilder cannot be null");
        setSharedObject(AuthenticationManagerBuilder.class, authenticationBuilder);
        for (Map.Entry<Class<? extends Object>, Object> entry : sharedObjects.entrySet()){
            setSharedObject((Class<Object>) entry.getKey(), entry.getValue());
        }
        ApplicationContext context = (ApplicationContext) sharedObjects.get(ApplicationContext.class);
        //this.requestMatcherConfigurer...
    }

    private ApplicationContext getContext(){
        return getSharedObject(ApplicationContext.class);
    }

    private <C extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>> C getOrApply(C configure) throws Exception{
        C existingConfig = (C) getConfigurer(configure.getClass());
        if (existingConfig != null){
            return existingConfig;
        }
        return apply(configure);
    }


    public <C> void setSharedObject(Class<C> sharedType, C object){
        super.setSharedObject(sharedType, object);
    }

    public CorsConfigurer<HttpSecurity> cors() throws Exception{
        return getOrApply(new CorsConfigurer<HttpSecurity>());
    }


    @Override
    public <C extends SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity>> C removeConfigurer(Class<C> clazz) {
        return null;
    }


    @Override
    public HttpSecurity authenticationProvider(AuthenticationProvider authenticationProvider) {
        return null;
    }

    @Override
    public HttpSecurity userDetailsService(UserDetailsService userDetailsService) throws Exception {
        return null;
    }

    @Override
    public HttpSecurity addFilterAfter(javax.servlet.Filter filter, Class<? extends javax.servlet.Filter> afterFilter) {
        return null;
    }

    @Override
    public HttpSecurity addFilterBefore(javax.servlet.Filter filter, Class<? extends javax.servlet.Filter> beforeFilter) {
        return null;
    }

    @Override
    public HttpSecurity addFilter(javax.servlet.Filter filter) {
        return null;
    }


    @Override
    protected DefaultSecurityFilterChain performBuild() throws Exception {
        return null;
    }
}
