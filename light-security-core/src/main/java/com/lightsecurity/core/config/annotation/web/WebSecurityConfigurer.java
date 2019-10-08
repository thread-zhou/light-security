package com.lightsecurity.core.config.annotation.web;

import com.lightsecurity.core.config.annotation.SecurityBuilder;
import com.lightsecurity.core.config.annotation.SecurityConfigurer;

import javax.servlet.Filter;

public interface WebSecurityConfigurer<T extends SecurityBuilder<Filter>> extends SecurityConfigurer<Filter, T> {
}
