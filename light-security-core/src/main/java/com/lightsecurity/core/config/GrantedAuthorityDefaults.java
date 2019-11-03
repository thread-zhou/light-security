package com.lightsecurity.core.config;
public final class GrantedAuthorityDefaults {

    private final String rolePrefix;

    public GrantedAuthorityDefaults(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    /**
     * The default prefix used with role based authorization. Default is "ROLE_".
     *
     * @return the default role prefix
     */
    public String getRolePrefix() {
        return this.rolePrefix;
    }
}

