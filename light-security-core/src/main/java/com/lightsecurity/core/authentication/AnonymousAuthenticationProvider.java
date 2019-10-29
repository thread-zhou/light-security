package com.lightsecurity.core.authentication;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.SpringSecurityMessageSource;
import com.lightsecurity.core.exception.AuthenticationException;
import com.lightsecurity.core.exception.BadCredentialsException;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;

public class AnonymousAuthenticationProvider implements AuthenticationProvider, MessageSourceAware {

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private String key;

    public AnonymousAuthenticationProvider(String key) {
        Assert.hasLength(key, "A Key is required");
        this.key = key;
    }

    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        if (!support(authentication.getClass())) {
            return null;
        }

        if (this.key.hashCode() != ((AnonymousAuthenticationToken) authentication)
                .getKeyHash()) {
            throw new BadCredentialsException(
                    messages.getMessage("AnonymousAuthenticationProvider.incorrectKey",
                            "The presented AnonymousAuthenticationToken does not contain the expected key"));
        }

        return authentication;
    }

    @Override
    public boolean support(Class<?> authentication) {
        return (AnonymousAuthenticationToken.class.isAssignableFrom(authentication));
    }

    public String getKey() {
        return key;
    }

    public void setMessageSource(MessageSource messageSource) {
        Assert.notNull(messageSource, "messageSource cannot be null");
        this.messages = new MessageSourceAccessor(messageSource);
    }
}
