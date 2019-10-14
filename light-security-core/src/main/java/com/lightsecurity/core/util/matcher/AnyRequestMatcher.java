package com.lightsecurity.core.util.matcher;

import javax.servlet.http.HttpServletRequest;

/**
 * 匹配任何提供的请求
 */
public final class AnyRequestMatcher implements RequestMatcher{

    public static final RequestMatcher INSTANCE = new AnyRequestMatcher();

    @Override
    public boolean matches(HttpServletRequest request) {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AnyRequestMatcher || obj instanceof com.lightsecurity.core.util.matcher.AnyRequestMatcher;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    private AnyRequestMatcher(){}
}
