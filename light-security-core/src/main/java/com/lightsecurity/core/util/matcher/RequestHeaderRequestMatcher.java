package com.lightsecurity.core.util.matcher;

import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;

public final class RequestHeaderRequestMatcher implements RequestMatcher {
    private final String expectedHeaderName;
    private final String expectedHeaderValue;

    /**
     * Creates a new instance that will match if a header by the name of
     * {@link #expectedHeaderName} is present. In this instance, the value does not
     * matter.
     *
     * @param expectedHeaderName the name of the expected header that if present the
     * request will match. Cannot be null.
     */
    public RequestHeaderRequestMatcher(String expectedHeaderName) {
        this(expectedHeaderName, null);
    }

    /**
     * Creates a new instance that will match if a header by the name of
     * {@link #expectedHeaderName} is present and if the {@link #expectedHeaderValue} is
     * non-null the first value is the same.
     *
     * @param expectedHeaderName the name of the expected header. Cannot be null
     * @param expectedHeaderValue the expected header value or null if the value does not
     * matter
     */
    public RequestHeaderRequestMatcher(String expectedHeaderName,
                                       String expectedHeaderValue) {
        Assert.notNull(expectedHeaderName, "headerName cannot be null");
        this.expectedHeaderName = expectedHeaderName;
        this.expectedHeaderValue = expectedHeaderValue;
    }

    public boolean matches(HttpServletRequest request) {
        String actualHeaderValue = request.getHeader(expectedHeaderName);
        if (expectedHeaderValue == null) {
            return actualHeaderValue != null;
        }

        return expectedHeaderValue.equals(actualHeaderValue);
    }

    @Override
    public String toString() {
        return "RequestHeaderRequestMatcher [expectedHeaderName=" + expectedHeaderName
                + ", expectedHeaderValue=" + expectedHeaderValue + "]";
    }
}
