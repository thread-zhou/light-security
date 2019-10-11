package com.lightsecurity.core.authentication.event;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.exception.AuthenticationException;

/**
 * 指示由于用户账户已过期导致身份验证失败的应用程序事件
 */
public class AuthenticationFailureExpiredEvent extends AbstractAuthenticationFailureEvent {

    public AuthenticationFailureExpiredEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication, exception);
    }
}
