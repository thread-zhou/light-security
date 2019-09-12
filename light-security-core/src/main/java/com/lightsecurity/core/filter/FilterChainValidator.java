package com.lightsecurity.core.filter;

/**
 * 过滤器链检验器
 */
public interface FilterChainValidator {

    void validate(FilterChainProxy filterChainProxy);

}
