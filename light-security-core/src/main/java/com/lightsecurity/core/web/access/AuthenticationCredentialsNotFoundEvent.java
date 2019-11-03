package com.lightsecurity.core.web.access;

import com.lightsecurity.core.exception.AuthenticationCredentialsNotFoundException;

import java.util.Collection;

public class AuthenticationCredentialsNotFoundEvent extends AbstractAuthorizationEvent {
    // ~ Instance fields
    // ================================================================================================

    private AuthenticationCredentialsNotFoundException credentialsNotFoundException;
    private Collection<ConfigAttribute> configAttribs;

    // ~ Constructors
    // ===================================================================================================

    /**
     * Construct the event.
     *
     * @param secureObject the secure object
     * @param attributes that apply to the secure object
     * @param credentialsNotFoundException exception returned to the caller (contains
     * reason)
     *
     */
    public AuthenticationCredentialsNotFoundEvent(Object secureObject,
                                                  Collection<ConfigAttribute> attributes,
                                                  AuthenticationCredentialsNotFoundException credentialsNotFoundException) {
        super(secureObject);

        if ((attributes == null) || (credentialsNotFoundException == null)) {
            throw new IllegalArgumentException(
                    "All parameters are required and cannot be null");
        }

        this.configAttribs = attributes;
        this.credentialsNotFoundException = credentialsNotFoundException;
    }

    // ~ Methods
    // ========================================================================================================

    public Collection<ConfigAttribute> getConfigAttributes() {
        return configAttribs;
    }

    public AuthenticationCredentialsNotFoundException getCredentialsNotFoundException() {
        return credentialsNotFoundException;
    }
}