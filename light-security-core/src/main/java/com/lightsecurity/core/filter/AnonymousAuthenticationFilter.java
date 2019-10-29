package com.lightsecurity.core.filter;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.GrantedAuthority;
import com.lightsecurity.core.authentication.AnonymousAuthenticationToken;
import com.lightsecurity.core.authentication.AuthenticationDetailsSource;
import com.lightsecurity.core.authentication.WebAuthenticationDetailsSource;
import com.lightsecurity.core.authority.AuthorityUtils;
import com.lightsecurity.core.context.SecurityContextHolder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public class AnonymousAuthenticationFilter extends GenericFilter implements InitializingBean {

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private String key;
    private Object principal;
    private List<GrantedAuthority> authorities;

    public AnonymousAuthenticationFilter(String key) {
        this(key, "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    }

    public AnonymousAuthenticationFilter(String key, Object principal,
                                         List<GrantedAuthority> authorities) {
        Assert.hasLength(key, "key cannot be null or empty");
        Assert.notNull(principal, "Anonymous authentication principal must be set");
        Assert.notNull(authorities, "Anonymous authorities must be set");
        this.key = key;
        this.principal = principal;
        this.authorities = authorities;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication() == null){
            SecurityContextHolder.getContext().setAuthentication(createAuthentication((HttpServletRequest) servletRequest));
            if (logger.isDebugEnabled()) {
                logger.debug("Populated SecurityContextHolder with anonymous token: '"
                        + SecurityContextHolder.getContext().getAuthentication() + "'");
            }
        }else {
            if (logger.isDebugEnabled()) {
                logger.debug("SecurityContextHolder not populated with anonymous token, as it already contained: '"
                        + SecurityContextHolder.getContext().getAuthentication() + "'");
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }

    private Authentication createAuthentication(HttpServletRequest request) {
        AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken(key,
                principal, authorities);
        auth.setDetails(authenticationDetailsSource.bindDetails(request));

        return auth;
    }

    public void setAuthenticationDetailsSource(
            AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        Assert.notNull(authenticationDetailsSource,
                "AuthenticationDetailsSource required");
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    public Object getPrincipal() {
        return principal;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        Assert.hasLength(key, "key must have length");
        Assert.notNull(principal, "Anonymous authentication principal must be set");
        Assert.notNull(authorities, "Anonymous authorities must be set");
    }
}
