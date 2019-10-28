package com.lightsecurity.core.exception;

public class UserDetailsServiceNotFoundException extends AuthenticationException {
    public UserDetailsServiceNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public UserDetailsServiceNotFoundException(String msg) {
        super(msg);
    }
}
