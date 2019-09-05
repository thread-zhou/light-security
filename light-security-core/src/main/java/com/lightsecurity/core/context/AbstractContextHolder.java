package com.lightsecurity.core.context;

import org.apache.commons.lang3.StringUtils;

/**
 * 基本实现
 */
public abstract class AbstractContextHolder<K, V> implements ContextHolder<K, V>{

    public static final String USER_INFO_SESSION_CONTEXT_HOLDER_STRATEGY = "USER_INFO_SESSION_CONTEXT_HOLDER_STRATEGY";
    public static final String GRANTED_AUTHORITY_CONTEXT_HOLDER_STRATEGY = "GRANTED_AUTHORITY_CONTEXT_HOLDER_STRATEGY";
    private final String strategyName;
    private AbstractContextHolderStrategy strategy;

    protected AbstractContextHolder(String strategyName){
        this.strategyName = strategyName;
        if (StringUtils.equals(strategyName, USER_INFO_SESSION_CONTEXT_HOLDER_STRATEGY)){
            strategy = new UserInfoSessionContextHolderHolderStrategy(new UserInfoSessionContext());
        }else if (StringUtils.equals(strategyName, GRANTED_AUTHORITY_CONTEXT_HOLDER_STRATEGY)){
            strategy = new GrantedAuthorityContextHolderHolderStrategy(new GrantedAuthorityContext());
        }else {
            throw new IllegalArgumentException("strategyName参数不合法");
        }
    }

    @Override
    public String getStrategyName() {
        return strategyName;
    }

    @Override
    public Context<K, V> getContextWrapper() {
        return strategy.getContextWrapper();
    }
}
