package com.lightsecurity.core.filter;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.MessageSourceAware;

public abstract class AbstractSecurityInterceptor implements InitializingBean,
        ApplicationEventPublisherAware, MessageSourceAware {

}
