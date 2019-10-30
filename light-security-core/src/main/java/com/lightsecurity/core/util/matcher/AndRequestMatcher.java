package com.lightsecurity.core.util.matcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

public final class AndRequestMatcher implements RequestMatcher {
    private final Log logger = LogFactory.getLog(getClass());

    private final List<RequestMatcher> requestMatchers;

    /**
     * Creates a new instance
     *
     * @param requestMatchers the {@link RequestMatcher} instances to try
     */
    public AndRequestMatcher(List<RequestMatcher> requestMatchers) {
        Assert.notEmpty(requestMatchers, "requestMatchers must contain a value");
        if (requestMatchers.contains(null)) {
            throw new IllegalArgumentException(
                    "requestMatchers cannot contain null values");
        }
        this.requestMatchers = requestMatchers;
    }

    /**
     * Creates a new instance
     *
     * @param requestMatchers the {@link RequestMatcher} instances to try
     */
    public AndRequestMatcher(RequestMatcher... requestMatchers) {
        this(Arrays.asList(requestMatchers));
    }

    public boolean matches(HttpServletRequest request) {
        for (RequestMatcher matcher : requestMatchers) {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to match using " + matcher);
            }
            if (!matcher.matches(request)) {
                logger.debug("Did not match");
                return false;
            }
        }
        logger.debug("All requestMatchers returned true");
        return true;
    }

    @Override
    public String toString() {
        return "AndRequestMatcher [requestMatchers=" + requestMatchers + "]";
    }
}
