package com.lightsecurity.core.authentication.event;

import com.lightsecurity.core.Authentication;

/**
 * 表示成功认证的应用程序事件
 */
public class AuthenticationSuccessEvent extends AbstractAuthenticationEvent {

    public AuthenticationSuccessEvent(Authentication authentication) {
        super(authentication);
    }
}
