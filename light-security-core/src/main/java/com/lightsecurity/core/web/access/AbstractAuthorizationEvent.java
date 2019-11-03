package com.lightsecurity.core.web.access;

import org.springframework.context.ApplicationEvent;

public abstract class AbstractAuthorizationEvent extends ApplicationEvent {
    // ~ Constructors
    // ===================================================================================================

    /**
     * Construct the event, passing in the secure object being intercepted.
     *
     * @param secureObject the secure object
     */
    public AbstractAuthorizationEvent(Object secureObject) {
        super(secureObject);
    }
}
