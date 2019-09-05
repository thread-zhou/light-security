package com.lightsecurity.core.context;

/**
 * 自实现容器持有者接口
 */
public interface ContextHolder<K, V> {

    String getStrategyName();

    Context<K, V> getContextWrapper();
}
