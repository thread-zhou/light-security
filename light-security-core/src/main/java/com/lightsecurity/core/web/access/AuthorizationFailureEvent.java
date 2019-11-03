package com.lightsecurity.core.web.access;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.exception.AccessDeniedException;

import java.util.Collection;

public class AuthorizationFailureEvent extends AbstractAuthorizationEvent {
    // ~ Instance fields
    // ================================================================================================

    private AccessDeniedException accessDeniedException;
    private Authentication authentication;
    private Collection<ConfigAttribute> configAttributes;

    // ~ Constructors
    // ===================================================================================================

    /**
     * Construct the event.
     *
     * @param secureObject the secure object
     * @param attributes that apply to the secure object
     * @param authentication that was found in the <code>SecurityContextHolder</code>
     * @param accessDeniedException that was returned by the
     * <code>AccessDecisionManager</code>
     *
     * @throws IllegalArgumentException if any null arguments are presented.
     */
    public AuthorizationFailureEvent(Object secureObject,
                                     Collection<ConfigAttribute> attributes, Authentication authentication,
                                     AccessDeniedException accessDeniedException) {
        super(secureObject);

        if ((attributes == null) || (authentication == null)
                || (accessDeniedException == null)) {
            throw new IllegalArgumentException(
                    "All parameters are required and cannot be null");
        }

        this.configAttributes = attributes;
        this.authentication = authentication;
        this.accessDeniedException = accessDeniedException;
    }

    // ~ Methods
    // ========================================================================================================

    public AccessDeniedException getAccessDeniedException() {
        return accessDeniedException;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public Collection<ConfigAttribute> getConfigAttributes() {
        return configAttributes;
    }
}
