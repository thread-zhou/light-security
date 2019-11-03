package com.lightsecurity.core.web.access;

import com.lightsecurity.core.GrantedAuthority;

import java.util.Collection;

public interface RoleHierarchy {

    public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(
            Collection<? extends GrantedAuthority> authorities);

}

