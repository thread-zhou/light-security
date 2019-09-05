package com.lightsecurity.core.context;

/**
 * A strategy for storing security context information against a thread.
 * 一种针对线程存储安全上下文信息的策略
 * 模仿spring security 中的 SecurityContextHolderStrategy
 *
 * 主要是适用于放置SecurityContext
 */
public interface SecurityContextHolderStrategy {

    /**
     * 清除当前的容器
     */
    void clearContext();

    /**
     * 获取当前的容器
     * @return
     */
    SecurityContext getContext();

    /**
     * 设置一个容器
     * @param context
     */
    void setContext(SecurityContext context);

    /**
     * 创建一个空内容的容器
     * @return
     */
    SecurityContext createEmptyContext();

}
