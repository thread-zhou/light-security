package com.lightsecurity.core.web.access;

import com.lightsecurity.core.Authentication;

import java.util.Collection;

public final class NullRunAsManager implements RunAsManager {
    // ~ Methods
    // ========================================================================================================

    public Authentication buildRunAs(Authentication authentication, Object object,
                                     Collection<ConfigAttribute> config) {
        return null;
    }

    public boolean supports(ConfigAttribute attribute) {
        return false;
    }

    public boolean supports(Class<?> clazz) {
        return true;
    }
}
