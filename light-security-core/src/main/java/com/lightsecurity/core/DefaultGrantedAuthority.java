package com.lightsecurity.core;

import org.springframework.util.Assert;

public final class DefaultGrantedAuthority implements GrantedAuthority {

    private final DefaultPermission permission;

    public DefaultGrantedAuthority(DefaultPermission permission){
        Assert.notNull(permission, "授权的权限数据是必须的");
        this.permission = permission;
    }

    public Permission getAuthority() {
        return permission;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }

        if (obj instanceof DefaultGrantedAuthority){
            return permission.equals((DefaultGrantedAuthority)obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.permission.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(":");
        sb.append(permission.toString());
        return sb.toString();
    }
}
