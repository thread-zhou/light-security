package com.lightsecurity.core.web.builders;

import com.lightsecurity.core.authority.AuthenticationManagerBuilder;
import com.lightsecurity.core.config.annotation.AbstractConfiguredSecurityBuilder;
import com.lightsecurity.core.config.annotation.ObjectPostProcessor;
import com.lightsecurity.core.config.annotation.SecurityBuilder;
import com.lightsecurity.core.filter.DefaultSecurityFilterChain;

import java.util.Map;

public final class HttpSecurity extends AbstractConfiguredSecurityBuilder<DefaultSecurityFilterChain, HttpSecurity> implements SecurityBuilder<DefaultSecurityFilterChain> {


    public HttpSecurity(ObjectPostProcessor<Object> objectPostProcessor,
                        AuthenticationManagerBuilder authenticationBuilder,
                        Map<Class<? extends Object>, Object> sharedObjects) {
        super(objectPostProcessor);
        //todo 完成HttpSecurity的编写
    }

    @Override
    protected DefaultSecurityFilterChain performBuild() throws Exception {
        return null;
    }
}
