package com.lightsecurity.core.authentication;

import com.lightsecurity.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationTrustResolverImpl implements AuthenticationTrustResolver {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Class<? extends Authentication> anonymousClass = AnonymousAuthenticationToken.class;
//    private Class<? extends Authentication> rememberMeClass = RememberMeAuthenticationToken.class;

    @Override
    public boolean isAnonymous(Authentication authentication) {
        if ((anonymousClass == null) || (authentication == null)) {
            return false;
        }

        return anonymousClass.isAssignableFrom(authentication.getClass());
    }

    @Override
    public boolean isRememberMe(Authentication authentication) {
        logger.warn("为实现该方法, 请自行实现");
        return false;
    }

    Class<? extends Authentication> getAnonymousClass() {
        return anonymousClass;
    }

//    Class<? extends Authentication> getRememberMeClass() {
//        return rememberMeClass;
//    }
    
    public void setAnonymousClass(Class<? extends Authentication> anonymousClass) {
        this.anonymousClass = anonymousClass;
    }

//    public void setRememberMeClass(Class<? extends Authentication> rememberMeClass) {
//        this.rememberMeClass = rememberMeClass;
//    }
}
