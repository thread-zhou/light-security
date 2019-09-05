package com.lightsecurity.core;

/**
 * 权限接口
 */
public interface Permission {

    /**
     * 获取权限指定拦截url
     * @return
     */
    String getPermissionPattern();

    /**
     * 获取权限请求限制的方法类型，如 post、get等
     * @return
     */
    String getPermissionMethod();

    /**
     * 权限是否可用
     * @return
     */
    boolean isEnabled();
}
