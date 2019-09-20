package com.lightsecurity.core;


import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * The default <code>MessageSource</code> used by Spring Security.
 * <p>
 * All Spring Security classes requiring messge localization will by default use this
 * class. However, all such classes will also implement <code>MessageSourceAware</code> so
 * that the application context can inject an alternative message source. Therefore this
 * class is only used when the deployment environment has not specified an alternative
 * message source.
 * </p>
 *
 * @author Ben Alex
 */
public class SpringSecurityMessageSource extends ResourceBundleMessageSource {
    // ~ Constructors
    // ===================================================================================================

    public SpringSecurityMessageSource() {
        setBasename("org.springframework.security.messages");
    }

    // ~ Methods
    // ========================================================================================================

    public static MessageSourceAccessor getAccessor() {
        return new MessageSourceAccessor(new SpringSecurityMessageSource());
    }
}
