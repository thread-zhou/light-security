package com.lightsecurity.core.authentication.event;

import com.lightsecurity.core.Authentication;

/**
 * 指示由于无效凭据导致身份验证失败的应用程序事件
 */
public class AuthenticationFailureBadCredentialsEvent extends AbstractAuthenticationEvent {

    public AuthenticationFailureBadCredentialsEvent(Authentication authentication) {
        super(authentication);
    }
}
