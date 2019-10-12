package com.lightsecurity.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import java.util.Set;


/**
 * 核心配置类
 */
@ConfigurationProperties(prefix = "light.security")
public class SecurityProperties {

    public static final int DEFAULT_FILTER_ORDER = FilterRegistrationBean.REQUEST_WRAPPER_FILTER_MAX_ORDER - 100;


    private int filterOrder = DEFAULT_FILTER_ORDER;

    /**
     * Light security filter Chain dispatcher types
     */
    private Set<String> filterDispatcherTypes;

    public int getFilterOrder() {
        return filterOrder;
    }

    public Set<String> getFilterDispatcherTypes() {
        return filterDispatcherTypes;
    }
}
