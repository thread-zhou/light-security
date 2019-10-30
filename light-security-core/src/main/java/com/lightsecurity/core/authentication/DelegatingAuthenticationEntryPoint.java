package com.lightsecurity.core.authentication;

import com.lightsecurity.core.exception.AuthenticationException;
import com.lightsecurity.core.util.matcher.RequestMatcher;
import com.lightsecurity.core.web.AuthenticationEntryPoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;

public class DelegatingAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {
    private final Log logger = LogFactory.getLog(getClass());

    private final LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints;
    private AuthenticationEntryPoint defaultEntryPoint;

    public DelegatingAuthenticationEntryPoint(
            LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints) {
        this.entryPoints = entryPoints;
    }

    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        for (RequestMatcher requestMatcher : entryPoints.keySet()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to match using " + requestMatcher);
            }
            if (requestMatcher.matches(request)) {
                AuthenticationEntryPoint entryPoint = entryPoints.get(requestMatcher);
                if (logger.isDebugEnabled()) {
                    logger.debug("Match found! Executing " + entryPoint);
                }
                entryPoint.commence(request, response, authException);
                return;
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("No match found. Using default entry point " + defaultEntryPoint);
        }

        // No EntryPoint matched, use defaultEntryPoint
        defaultEntryPoint.commence(request, response, authException);
    }

    /**
     * EntryPoint which is used when no RequestMatcher returned true
     */
    public void setDefaultEntryPoint(AuthenticationEntryPoint defaultEntryPoint) {
        this.defaultEntryPoint = defaultEntryPoint;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notEmpty(entryPoints, "entryPoints must be specified");
        Assert.notNull(defaultEntryPoint, "defaultEntryPoint must be specified");
    }
}
