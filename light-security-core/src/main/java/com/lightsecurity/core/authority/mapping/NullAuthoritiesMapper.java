package com.lightsecurity.core.authority.mapping;


import com.lightsecurity.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Luke Taylor
 */
public class NullAuthoritiesMapper implements GrantedAuthoritiesMapper {
    public Collection<? extends GrantedAuthority> mapAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        return authorities;
    }
}
