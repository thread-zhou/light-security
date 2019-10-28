package com.lightsecurity.core.web.builders;

import com.lightsecurity.core.authentication.AuthenticationManager;
import com.lightsecurity.core.authentication.AuthenticationProvider;
import com.lightsecurity.core.authority.AuthenticationManagerBuilder;
import com.lightsecurity.core.config.annotation.*;
import com.lightsecurity.core.config.annotation.web.HttpSecurityBuilder;
import com.lightsecurity.core.filter.DefaultSecurityFilterChain;
import com.lightsecurity.core.userdetails.UserDetailsService;
import com.lightsecurity.core.util.matcher.AnyRequestMatcher;
import com.lightsecurity.core.util.matcher.RequestMatcher;
import com.lightsecurity.core.web.configurers.CorsConfigurer;
import com.lightsecurity.core.web.configurers.FormLoginConfigurer;
import com.lightsecurity.core.web.configurers.SecurityContextConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class HttpSecurity extends AbstractConfiguredSecurityBuilder<DefaultSecurityFilterChain, HttpSecurity>
        implements SecurityBuilder<DefaultSecurityFilterChain>, HttpSecurityBuilder<HttpSecurity> {

    private List<Filter> filters = new ArrayList<>();

    /**
     * 匹配任何的request
     */
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

    private AuthenticationManagerBuilder getAuthenticationRegistry(){
        return getSharedObject(AuthenticationManagerBuilder.class);
    }




    public CorsConfigurer<HttpSecurity> cors() throws Exception{
        return getOrApply(new CorsConfigurer<HttpSecurity>());
    }


    public FormLoginConfigurer<HttpSecurity> formLogin() throws Exception {
        return getOrApply(new FormLoginConfigurer<HttpSecurity>());
    }


    public SecurityContextConfigurer<HttpSecurity> securityContext()throws Exception{
        return getOrApply(new SecurityContextConfigurer<HttpSecurity>());
    }

    @Override
    public <C> void setSharedObject(Class<C> sharedType, C object){
        super.setSharedObject(sharedType, object);
    }

    @Override
    public HttpSecurity authenticationProvider(AuthenticationProvider authenticationProvider) {
        getAuthenticationRegistry().authenticationProvider(authenticationProvider);
        return this;
    }

    @Override
    public HttpSecurity userDetailsService(UserDetailsService userDetailsService) throws Exception {
        //todo 等待实现
        return null;
    }

    @Override
    public HttpSecurity addFilterAfter(javax.servlet.Filter filter, Class<? extends javax.servlet.Filter> afterFilter) {
        comparator.registerAfter(filter.getClass(), afterFilter);
        return addFilter(filter);
    }

    @Override
    public HttpSecurity addFilterBefore(javax.servlet.Filter filter, Class<? extends javax.servlet.Filter> beforeFilter) {
        comparator.registerBefore(filter.getClass(), beforeFilter);
        return addFilter(filter);
    }



    @Override
    public HttpSecurity addFilter(javax.servlet.Filter filter) {
        Class<? extends Filter> filterClass = filter.getClass();
        //没有在FilterComparator中注册的不能直接使用addFilter进行注册, 需要使用addFilter**的方式先进性注册再调用addFilter方法
        if (!comparator.isRegistered(filterClass)){
            throw new IllegalArgumentException(
                    "The Filter class "
                            + filterClass.getName()
                            + " does not have a registered order and cannot be added without a specified order. Consider using addFilterBefore or addFilterAfter instead.");
        }
        this.filters.add(filter);
        return this;
    }

    @Override
    protected void beforeConfigure() throws Exception {
        setSharedObject(AuthenticationManager.class, getAuthenticationRegistry().build());
    }

    @Override
    protected DefaultSecurityFilterChain performBuild() throws Exception {
        //使用FilterComparator进行Filter的排序
        Collections.sort(filters, comparator);
        return new DefaultSecurityFilterChain(requestMatcher, filters);
    }

    public HttpSecurity addFilterAt(Filter filter, Class<? extends Filter> atFilter) {
        this.comparator.registerAt(filter.getClass(), atFilter);
        return addFilter(filter);
    }
}
