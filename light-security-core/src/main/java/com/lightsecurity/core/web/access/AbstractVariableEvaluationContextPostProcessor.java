package com.lightsecurity.core.web.access;

import com.lightsecurity.core.web.FilterInvocation;
import org.springframework.expression.EvaluationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

abstract class AbstractVariableEvaluationContextPostProcessor
        implements EvaluationContextPostProcessor<FilterInvocation> {

    @Override
    public final EvaluationContext postProcess(EvaluationContext context,
                                               FilterInvocation invocation) {
        final HttpServletRequest request = invocation.getHttpRequest();
        return new DelegatingEvaluationContext(context) {
            private Map<String, String> variables;

            @Override
            public Object lookupVariable(String name) {
                Object result = super.lookupVariable(name);
                if (result != null) {
                    return result;
                }
                if (this.variables == null) {
                    this.variables = extractVariables(request);
                }
                return this.variables.get(name);
            }

        };
    }

    abstract Map<String, String> extractVariables(HttpServletRequest request);

}

