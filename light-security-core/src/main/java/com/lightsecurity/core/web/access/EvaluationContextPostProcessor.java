package com.lightsecurity.core.web.access;

import org.springframework.expression.EvaluationContext;

interface EvaluationContextPostProcessor<I> {

    /**
     * Allows post processing of the {@link EvaluationContext}. Implementations
     * may return a new instance of {@link EvaluationContext} or modify the
     * {@link EvaluationContext} that was passed in.
     *
     * @param context
     *            the original {@link EvaluationContext}
     * @param invocation
     *            the security invocation object (i.e. FilterInvocation)
     * @return the upated context.
     */
    EvaluationContext postProcess(EvaluationContext context, I invocation);
}
