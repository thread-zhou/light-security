package com.lightsecurity.core.context;

import org.apache.commons.lang3.StringUtils;

/**
 * SecurityContext的容器实例持有者
 */
public class SecurityContextHolder {

    private static final String SYSTEM_PROPERTIES = "DEFAULT";
    private static final String strategyName = SYSTEM_PROPERTIES;
    //容器实现
    private static SecurityContextHolderStrategy strategy;

    static {
        initialize();
    }

    private static void initialize() {
        if (StringUtils.equals(SYSTEM_PROPERTIES, strategyName)){
            strategy = new ThreadLocalSecurityContextHolderStrategy();
        }else {
            throw new IllegalArgumentException("暂时不支持对 strategyName 参数的设置");
        }
    }

    public static SecurityContext getContext(){
        return strategy.getContext();
    }

    public static void clearContext(){
        strategy.clearContext();
    }

    public static void setContext(SecurityContext context){
        strategy.setContext(context);
    }

    public static SecurityContextHolderStrategy getStrategy() {
        return strategy;
    }

    public static SecurityContext createEmptyContext(){
        return strategy.createEmptyContext();
    }

    public String toString() {
        return "SecurityContextHolder[strategy='" + strategyName + "]";
    }
}
