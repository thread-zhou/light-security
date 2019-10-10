package com.lightsecurity.core.authority;

import com.lightsecurity.core.authentication.AuthenticationManager;
import com.lightsecurity.core.authentication.ProviderManagerBuilder;
import com.lightsecurity.core.config.annotation.AbstractConfiguredSecurityBuilder;
import com.lightsecurity.core.config.annotation.ObjectPostProcessor;

public class AuthenticationManagerBuilder extends AbstractConfiguredSecurityBuilder<AuthenticationManager, AuthenticationManagerBuilder> implements ProviderManagerBuilder<AuthenticationManagerBuilder> {


    public AuthenticationManagerBuilder(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    @Override
    protected AuthenticationManager performBuild() throws Exception {
        return null;
    }

    @Override
    public AuthenticationManagerBuilder authenticationProvider(AuthenticationProvider authenticationProvider) {
        return null;
    }

    //todo 完成AuthenticationManagerBuilder的编写
}
