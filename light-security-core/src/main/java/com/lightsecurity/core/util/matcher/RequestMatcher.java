package com.lightsecurity.core.util.matcher;

import javax.servlet.http.HttpServletRequest;

/**
 *用于比对 HttpServletRequest
 */
public interface RequestMatcher {

    /**
     * 根据当前request，找到合适的SecurityFilterChain
     * 每个request最多只会经过一个SecurityFilterChain
     * @param request
     * @return
     */
    boolean matches(HttpServletRequest request);

}
