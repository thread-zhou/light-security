package com.lightsecurity.core.authentication;

import com.lightsecurity.core.Authentication;

public class AuthenticationTrustResolverImpl implements AuthenticationTrustResolver {

//    private Class<? extends Authentication> anonymousClass = AnonymousAuthenticationToken.class;
//    private Class<? extends Authentication> rememberMeClass = RememberMeAuthenticationToken.class;

    @Override
    public boolean isAnonymous(Authentication authentication) {
        return false;
    }

    @Override
    public boolean isRememberMe(Authentication authentication) {
        return false;
    }
}
