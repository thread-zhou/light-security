package com.lightsecurity.core.authentication.dao;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.LightSecurityMessageSource;
import com.lightsecurity.core.authentication.AuthenticationProvider;
import com.lightsecurity.core.authentication.UsernamePasswordAuthenticationToken;
import com.lightsecurity.core.exception.AuthenticationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;

public abstract class AbstractUserDetailsAuthenticationProvider implements
        AuthenticationProvider, InitializingBean, MessageSourceAware {


    protected MessageSourceAccessor messages = LightSecurityMessageSource.getAccessor();
    //todo


    @Override
    public boolean support(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
                messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.onlySupports",
                        "Only UsernamePasswordAuthenticationToken is supported"));
        //todo
        return null;
    }
}
