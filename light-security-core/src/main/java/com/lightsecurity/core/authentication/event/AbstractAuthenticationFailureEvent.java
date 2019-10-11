package com.lightsecurity.core.authentication.event;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.exception.AuthenticationException;
import org.springframework.util.Assert;

public class AbstractAuthenticationFailureEvent extends AbstractAuthenticationEvent {

    private final AuthenticationException exception;


    public AbstractAuthenticationFailureEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication);
        Assert.notNull(exception, "AuthenticationException is required");
        this.exception = exception;
    }

    public AuthenticationException getException() {
        return exception;
    }
}
