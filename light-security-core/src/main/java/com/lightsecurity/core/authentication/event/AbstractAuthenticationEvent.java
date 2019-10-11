package com.lightsecurity.core.authentication.event;

import com.lightsecurity.core.Authentication;
import org.springframework.context.ApplicationEvent;

public abstract class AbstractAuthenticationEvent extends ApplicationEvent {


    public AbstractAuthenticationEvent(Authentication authentication) {
        super(authentication);
    }

    public Authentication getAuthentication(){
        return (Authentication) super.getSource();
    }
}
