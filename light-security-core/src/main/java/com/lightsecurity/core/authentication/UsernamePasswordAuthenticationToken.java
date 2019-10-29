package com.lightsecurity.core.authentication;

import com.lightsecurity.core.GrantedAuthority;

import java.util.Collection;

public class UsernamePasswordAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private Object credential;

    public UsernamePasswordAuthenticationToken(Object principal, Object credential, Collection<? extends GrantedAuthority> authorities){
        super(authorities);
        this.principal = principal;
        this.credential = credential;
        super.setAuthenticated(true);
    }

    public UsernamePasswordAuthenticationToken(Object principal, Object credential){
        super(null);
        this.principal = principal;
        this.credential = credential;
        super.setAuthenticated(false);
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated){
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    public Object getCredential() {
        return credential;
    }

    @Override
    public Object getCredentials() {
        return this.credential;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credential = null;
    }
}
