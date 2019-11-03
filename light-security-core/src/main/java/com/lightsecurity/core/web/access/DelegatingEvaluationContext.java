package com.lightsecurity.core.web.access;

import org.springframework.expression.*;

import java.util.List;

class DelegatingEvaluationContext implements EvaluationContext {
    private final EvaluationContext delegate;

    public DelegatingEvaluationContext(EvaluationContext delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    public TypedValue getRootObject() {
        return this.delegate.getRootObject();
    }

    @Override
    public List<ConstructorResolver> getConstructorResolvers() {
        return this.delegate.getConstructorResolvers();
    }

    @Override
    public List<MethodResolver> getMethodResolvers() {
        return this.delegate.getMethodResolvers();
    }

    @Override
    public List<PropertyAccessor> getPropertyAccessors() {
        return this.delegate.getPropertyAccessors();
    }

    @Override
    public TypeLocator getTypeLocator() {
        return this.delegate.getTypeLocator();
    }

    @Override
    public TypeConverter getTypeConverter() {
        return this.delegate.getTypeConverter();
    }

    @Override
    public TypeComparator getTypeComparator() {
        return this.delegate.getTypeComparator();
    }

    @Override
    public OperatorOverloader getOperatorOverloader() {
        return this.delegate.getOperatorOverloader();
    }

    @Override
    public BeanResolver getBeanResolver() {
        return this.delegate.getBeanResolver();
    }

    @Override
    public void setVariable(String name, Object value) {
        this.delegate.setVariable(name, value);
    }

    @Override
    public Object lookupVariable(String name) {
        return this.delegate.lookupVariable(name);
    }
}