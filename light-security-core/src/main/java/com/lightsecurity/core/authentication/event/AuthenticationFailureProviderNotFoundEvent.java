package com.lightsecurity.core.authentication.event;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.exception.AuthenticationException;

/**
 * 指示由于没有注册<code>AuthenticationProvider</code>可以处理请求而导致身份验证失败的应用程序事件
 */
public class AuthenticationFailureProviderNotFoundEvent extends AbstractAuthenticationFailureEvent {

    public AuthenticationFailureProviderNotFoundEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication, exception);
    }
}
