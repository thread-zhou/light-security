package com.lightsecurity.core;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

public class LightSecurityMessageSource extends ResourceBundleMessageSource {

    public LightSecurityMessageSource(){
        setBasename("com.light.security.messages");
    }

    public static MessageSourceAccessor getAccessor(){
        return new MessageSourceAccessor(new LightSecurityMessageSource());
    }

}
