package com.lightsecurity.core.web.access;

public class PublicInvocationEvent extends AbstractAuthorizationEvent {
    // ~ Constructors
    // ===================================================================================================

    /**
     * Construct the event, passing in the public secure object.
     *
     * @param secureObject the public secure object
     */
    public PublicInvocationEvent(Object secureObject) {
        super(secureObject);
    }
}
