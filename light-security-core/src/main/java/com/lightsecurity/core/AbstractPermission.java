package com.lightsecurity.core;

import org.apache.commons.lang3.StringUtils;

/**
 * Permission接口的通用实现
 */
public abstract class AbstractPermission implements Permission {

    protected final String permissionName;
    protected final String permissionPattern;
    protected final boolean enabled;

    protected AbstractPermission(String name, String pattern, boolean enabled){
        if (StringUtils.isBlank(pattern)){
            throw new IllegalArgumentException("构造器不接受空值或null参数");
        }
        this.permissionName = name;
        this.permissionPattern = pattern;
        this.enabled = enabled;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public String getPermissionPattern() {
        return permissionPattern;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public int compareTo(Permission authority) {
        return 0;
    }
}
