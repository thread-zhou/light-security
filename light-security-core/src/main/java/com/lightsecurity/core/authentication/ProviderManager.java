package com.lightsecurity.core.authentication;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.exception.AuthenticationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

public class ProviderManager implements AuthenticationManager, MessageSourceAware, InitializingBean {


    private boolean eraseCredentialsAfterAuthentication = true;

    public boolean isEraseCredentialsAfterAuthentication() {
        return eraseCredentialsAfterAuthentication;
    }
    //todo

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setMessageSource(MessageSource messageSource) {

    }
}
