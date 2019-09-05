package com.lightsecurity.core.context;

import com.lightsecurity.core.GrantedAuthority;

import java.util.Collection;

/**
 * 权限存储的Holder实现
 */
public final class GrantedAuthorityContextHolder extends AbstractContextHolder<String, Collection<GrantedAuthority>> {

    public GrantedAuthorityContextHolder() {
        super(GRANTED_AUTHORITY_CONTEXT_HOLDER_STRATEGY);
    }
}
