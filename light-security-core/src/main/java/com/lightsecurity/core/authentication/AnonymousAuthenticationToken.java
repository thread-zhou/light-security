package com.lightsecurity.core.authentication;

import com.lightsecurity.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Collection;

public class AnonymousAuthenticationToken extends AbstractAuthenticationToken implements Serializable {

    private final Object principal;
    private final int keyHash;

    public AnonymousAuthenticationToken(String key, Object principal,
                                         Collection<? extends GrantedAuthority> authorities) {
        this(extractKeyHash(key), principal, authorities);
    }

    private AnonymousAuthenticationToken(Integer keyHash, Object principal,
                                         Collection<? extends GrantedAuthority> authorities) {
        super(authorities);

        if (principal == null || "".equals(principal)) {
            throw new IllegalArgumentException("principal cannot be null or empty");
        }
        Assert.notEmpty(authorities, "authorities cannot be null or empty");

        this.keyHash = keyHash;
        this.principal = principal;
        setAuthenticated(true);
    }

    private static Integer extractKeyHash(String key) {
        Assert.hasLength(key, "key cannot be empty or null");
        return key.hashCode();
    }

    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (obj instanceof AnonymousAuthenticationToken) {
            AnonymousAuthenticationToken test = (AnonymousAuthenticationToken) obj;

            if (this.getKeyHash() != test.getKeyHash()) {
                return false;
            }

            return true;
        }

        return false;
    }

    public Object getCredentials() {
        return "";
    }

    public int getKeyHash() {
        return this.keyHash;
    }

    public Object getPrincipal() {
        return this.principal;
    }


}
