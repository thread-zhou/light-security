package com.lightsecurity.core.util.matcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public final class MediaTypeRequestMatcher implements RequestMatcher {
    private final Log logger = LogFactory.getLog(getClass());
    private final ContentNegotiationStrategy contentNegotiationStrategy;
    private final Collection<MediaType> matchingMediaTypes;
    private boolean useEquals;
    private Set<MediaType> ignoredMediaTypes = Collections.emptySet();

    /**
     * Creates an instance
     * @param contentNegotiationStrategy the {@link ContentNegotiationStrategy} to use
     * @param matchingMediaTypes the {@link MediaType} that will make the
     * {@link RequestMatcher} return true
     */
    public MediaTypeRequestMatcher(ContentNegotiationStrategy contentNegotiationStrategy,
                                   MediaType... matchingMediaTypes) {
        this(contentNegotiationStrategy, Arrays.asList(matchingMediaTypes));
    }

    /**
     * Creates an instance
     * @param contentNegotiationStrategy the {@link ContentNegotiationStrategy} to use
     * @param matchingMediaTypes the {@link MediaType} that will make the
     * {@link RequestMatcher} return true
     */
    public MediaTypeRequestMatcher(ContentNegotiationStrategy contentNegotiationStrategy,
                                   Collection<MediaType> matchingMediaTypes) {
        Assert.notNull(contentNegotiationStrategy,
                "ContentNegotiationStrategy cannot be null");
        Assert.notEmpty(matchingMediaTypes, "matchingMediaTypes cannot be null or empty");
        this.contentNegotiationStrategy = contentNegotiationStrategy;
        this.matchingMediaTypes = matchingMediaTypes;
    }

    public boolean matches(HttpServletRequest request) {
        List<MediaType> httpRequestMediaTypes;
        try {
            httpRequestMediaTypes = this.contentNegotiationStrategy
                    .resolveMediaTypes(new ServletWebRequest(request));
        }
        catch (HttpMediaTypeNotAcceptableException e) {
            this.logger.debug("Failed to parse MediaTypes, returning false", e);
            return false;
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("httpRequestMediaTypes=" + httpRequestMediaTypes);
        }
        for (MediaType httpRequestMediaType : httpRequestMediaTypes) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Processing " + httpRequestMediaType);
            }
            if (shouldIgnore(httpRequestMediaType)) {
                this.logger.debug("Ignoring");
                continue;
            }
            if (this.useEquals) {
                boolean isEqualTo = this.matchingMediaTypes
                        .contains(httpRequestMediaType);
                this.logger.debug("isEqualTo " + isEqualTo);
                return isEqualTo;
            }
            for (MediaType matchingMediaType : this.matchingMediaTypes) {
                boolean isCompatibleWith = matchingMediaType
                        .isCompatibleWith(httpRequestMediaType);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug(matchingMediaType + " .isCompatibleWith "
                            + httpRequestMediaType + " = " + isCompatibleWith);
                }
                if (isCompatibleWith) {
                    return true;
                }
            }
        }
        this.logger.debug("Did not match any media types");
        return false;
    }

    private boolean shouldIgnore(MediaType httpRequestMediaType) {
        for (MediaType ignoredMediaType : this.ignoredMediaTypes) {
            if (httpRequestMediaType.includes(ignoredMediaType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * If set to true, matches on exact {@link MediaType}, else uses
     * {@link MediaType#isCompatibleWith(MediaType)}.
     *
     * @param useEquals specify if equals comparison should be used.
     */
    public void setUseEquals(boolean useEquals) {
        this.useEquals = useEquals;
    }

    /**
     * Set the {@link MediaType} to ignore from the {@link ContentNegotiationStrategy}.
     * This is useful if for example, you want to match on
     * {@link MediaType#APPLICATION_JSON} but want to ignore {@link MediaType#ALL}.
     *
     * @param ignoredMediaTypes the {@link MediaType}'s to ignore from the
     * {@link ContentNegotiationStrategy}
     */
    public void setIgnoredMediaTypes(Set<MediaType> ignoredMediaTypes) {
        this.ignoredMediaTypes = ignoredMediaTypes;
    }

    @Override
    public String toString() {
        return "MediaTypeRequestMatcher [contentNegotiationStrategy="
                + this.contentNegotiationStrategy + ", matchingMediaTypes="
                + this.matchingMediaTypes + ", useEquals=" + this.useEquals
                + ", ignoredMediaTypes=" + this.ignoredMediaTypes + "]";
    }
}
