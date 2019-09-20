package com.lightsecurity.core.web.context;

import org.springframework.web.WebApplicationInitializer;

public abstract class AbstractSecurityWebApplicationInitializer implements WebApplicationInitializer {

    public static final String DEFAULT_FILTER_NAME = "lightSecurityFilterChain";

}
