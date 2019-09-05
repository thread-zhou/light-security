package com.lightsecurity.core.context;

import com.lightsecurity.core.GrantedAuthority;

import java.util.Collection;

/**
 * 权限容器实现
 */
public final class GrantedAuthorityContextHolderHolderStrategy extends AbstractContextHolderStrategy<String, Collection<GrantedAuthority>> {

    public GrantedAuthorityContextHolderHolderStrategy(GrantedAuthorityContext context) {
        super(context);
    }

    public static void main(String[] args) {
        GrantedAuthorityContextHolderHolderStrategy strategy = new GrantedAuthorityContextHolderHolderStrategy(new GrantedAuthorityContext());
        System.out.println(strategy.getContextWrapper());
    }
}
