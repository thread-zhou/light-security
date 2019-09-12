package com.lightsecurity.core.util.matcher;

import javax.servlet.http.HttpServletRequest;

/**
 *用于比对 HttpServletRequest
 */
public interface RequestMatcher {

    boolean matches(HttpServletRequest request);

}
