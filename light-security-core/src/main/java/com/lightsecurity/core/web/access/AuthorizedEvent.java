package com.lightsecurity.core.web.access;

import com.lightsecurity.core.Authentication;

import java.util.Collection;

public class AuthorizedEvent extends AbstractAuthorizationEvent {
    // ~ Instance fields
    // ================================================================================================

    private Authentication authentication;
    private Collection<ConfigAttribute> configAttributes;

    // ~ Constructors
    // ===================================================================================================

    /**
     * Construct the event.
     *
     * @param secureObject the secure object
     * @param attributes that apply to the secure object
     * @param authentication that successfully called the secure object
     *
     */
    public AuthorizedEvent(Object secureObject, Collection<ConfigAttribute> attributes,
                           Authentication authentication) {
        super(secureObject);

        if ((attributes == null) || (authentication == null)) {
            throw new IllegalArgumentException(
                    "All parameters are required and cannot be null");
        }

        this.configAttributes = attributes;
        this.authentication = authentication;
    }

    // ~ Methods
    // ========================================================================================================

    public Authentication getAuthentication() {
        return authentication;
    }

    public Collection<ConfigAttribute> getConfigAttributes() {
        return configAttributes;
    }
}
