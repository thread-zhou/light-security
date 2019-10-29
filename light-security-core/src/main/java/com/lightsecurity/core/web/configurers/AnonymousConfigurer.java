package com.lightsecurity.core.web.configurers;

import com.lightsecurity.core.GrantedAuthority;
import com.lightsecurity.core.authentication.AnonymousAuthenticationProvider;
import com.lightsecurity.core.authentication.AuthenticationProvider;
import com.lightsecurity.core.authority.AuthorityUtils;
import com.lightsecurity.core.config.annotation.web.HttpSecurityBuilder;
import com.lightsecurity.core.filter.AnonymousAuthenticationFilter;

import java.util.List;
import java.util.UUID;

public class AnonymousConfigurer<H extends HttpSecurityBuilder<H>> extends AbstractHttpConfigurer<AnonymousConfigurer<H>, H> {

    private String key;
    private AuthenticationProvider authenticationProvider;
    private AnonymousAuthenticationFilter authenticationFilter;
    private Object principal = "anonymousUser";
    private List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");

    public AnonymousConfigurer() {
    }

    public AnonymousConfigurer<H> key(String key) {
        this.key = key;
        return this;
    }

    public AnonymousConfigurer<H> principal(Object principal) {
        this.principal = principal;
        return this;
    }

    public AnonymousConfigurer<H> authorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
        return this;
    }

    public AnonymousConfigurer<H> authorities(String... authorities) {
        return authorities(AuthorityUtils.createAuthorityList(authorities));
    }

    public AnonymousConfigurer<H> authenticationProvider(
            AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
        return this;
    }

    public AnonymousConfigurer<H> authenticationFilter(
            AnonymousAuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
        return this;
    }

    @Override
    public void init(H http) throws Exception {
        if (authenticationProvider == null) {
            authenticationProvider = new AnonymousAuthenticationProvider(getKey());
        }
        if (authenticationFilter == null) {
            authenticationFilter = new AnonymousAuthenticationFilter(getKey(), principal,
                    authorities);
        }
        authenticationProvider = postProcess(authenticationProvider);
        http.authenticationProvider(authenticationProvider);
    }

    @Override
    public void configure(H http) throws Exception {
        authenticationFilter.afterPropertiesSet();
        http.addFilter(authenticationFilter);
    }

    private String getKey() {
        if (key == null) {
            key = UUID.randomUUID().toString();
        }
        return key;
    }
}
