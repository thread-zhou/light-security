package com.lightsecurity.core.authentication.event;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.exception.AuthenticationException;

/**
 * 指示由于用户凭据过期而导致的身份验证失败的应用程序事件
 */
public class AuthenticationFailureCredentialsExpiredEvent extends AbstractAuthenticationFailureEvent {

    public AuthenticationFailureCredentialsExpiredEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication, exception);
    }
}
