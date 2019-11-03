package com.lightsecurity.core.web.access;

import com.lightsecurity.core.web.FilterInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

class WebExpressionConfigAttribute implements ConfigAttribute,
        EvaluationContextPostProcessor<FilterInvocation> {
    private final Expression authorizeExpression;
    private final EvaluationContextPostProcessor<FilterInvocation> postProcessor;

    public WebExpressionConfigAttribute(Expression authorizeExpression,
                                        EvaluationContextPostProcessor<FilterInvocation> postProcessor) {
        this.authorizeExpression = authorizeExpression;
        this.postProcessor = postProcessor;
    }

    Expression getAuthorizeExpression() {
        return this.authorizeExpression;
    }

    @Override
    public EvaluationContext postProcess(EvaluationContext context, FilterInvocation fi) {
        return this.postProcessor == null ? context
                : this.postProcessor.postProcess(context, fi);
    }

    @Override
    public String getAttribute() {
        return null;
    }

    @Override
    public String toString() {
        return this.authorizeExpression.getExpressionString();
    }
}
