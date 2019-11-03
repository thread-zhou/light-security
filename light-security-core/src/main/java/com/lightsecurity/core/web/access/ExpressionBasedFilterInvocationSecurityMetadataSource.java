package com.lightsecurity.core.web.access;

import com.lightsecurity.core.util.matcher.AntPathRequestMatcher;
import com.lightsecurity.core.util.matcher.RequestMatcher;
import com.lightsecurity.core.util.matcher.RequestVariablesExtractor;
import com.lightsecurity.core.web.FilterInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ExpressionBasedFilterInvocationSecurityMetadataSource
        extends DefaultFilterInvocationSecurityMetadataSource {
    private final static Log logger = LogFactory
            .getLog(ExpressionBasedFilterInvocationSecurityMetadataSource.class);

    public ExpressionBasedFilterInvocationSecurityMetadataSource(
            LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestMap,
            SecurityExpressionHandler<FilterInvocation> expressionHandler) {
        super(processMap(requestMap, expressionHandler.getExpressionParser()));
        Assert.notNull(expressionHandler,
                "A non-null SecurityExpressionHandler is required");
    }

    private static LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> processMap(
            LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestMap,
            ExpressionParser parser) {
        Assert.notNull(parser, "SecurityExpressionHandler returned a null parser object");

        LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestToExpressionAttributesMap = new LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>(
                requestMap);

        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : requestMap
                .entrySet()) {
            RequestMatcher request = entry.getKey();
            Assert.isTrue(entry.getValue().size() == 1,
                    "Expected a single expression attribute for " + request);
            ArrayList<ConfigAttribute> attributes = new ArrayList<ConfigAttribute>(1);
            String expression = entry.getValue().toArray(new ConfigAttribute[1])[0]
                    .getAttribute();
            logger.debug("Adding web access control expression '" + expression + "', for "
                    + request);

            AbstractVariableEvaluationContextPostProcessor postProcessor = createPostProcessor(
                    request);
            try {
                attributes.add(new WebExpressionConfigAttribute(
                        parser.parseExpression(expression), postProcessor));
            }
            catch (ParseException e) {
                throw new IllegalArgumentException(
                        "Failed to parse expression '" + expression + "'");
            }

            requestToExpressionAttributesMap.put(request, attributes);
        }

        return requestToExpressionAttributesMap;
    }

    private static AbstractVariableEvaluationContextPostProcessor createPostProcessor(
            Object request) {
        if (request instanceof RequestVariablesExtractor) {
            return new RequestVariablesExtractorEvaluationContextPostProcessor(
                    (RequestVariablesExtractor) request);
        }
        return null;
    }

    static class AntPathMatcherEvaluationContextPostProcessor
            extends AbstractVariableEvaluationContextPostProcessor {
        private final AntPathRequestMatcher matcher;

        public AntPathMatcherEvaluationContextPostProcessor(
                AntPathRequestMatcher matcher) {
            this.matcher = matcher;
        }

        @Override
        Map<String, String> extractVariables(HttpServletRequest request) {
            return this.matcher.extractUriTemplateVariables(request);
        }
    }

    static class RequestVariablesExtractorEvaluationContextPostProcessor
            extends AbstractVariableEvaluationContextPostProcessor {
        private final RequestVariablesExtractor matcher;

        public RequestVariablesExtractorEvaluationContextPostProcessor(
                RequestVariablesExtractor matcher) {
            this.matcher = matcher;
        }

        @Override
        Map<String, String> extractVariables(HttpServletRequest request) {
            return this.matcher.extractUriTemplateVariables(request);
        }
    }

}
