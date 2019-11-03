package com.lightsecurity.core.web.access;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.authentication.AuthenticationTrustResolver;
import com.lightsecurity.core.authentication.AuthenticationTrustResolverImpl;
import com.lightsecurity.core.web.FilterInvocation;
import org.springframework.util.Assert;

public class DefaultWebSecurityExpressionHandler extends
        AbstractSecurityExpressionHandler<FilterInvocation> implements
        SecurityExpressionHandler<FilterInvocation> {

    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    private String defaultRolePrefix = "ROLE_";

    @Override
    protected SecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication, FilterInvocation fi) {
        WebSecurityExpressionRoot root = new WebSecurityExpressionRoot(authentication, fi);
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        root.setDefaultRolePrefix(this.defaultRolePrefix);
        return root;
    }


    public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
        Assert.notNull(trustResolver, "trustResolver cannot be null");
        this.trustResolver = trustResolver;
    }


    public void setDefaultRolePrefix(String defaultRolePrefix) {
        this.defaultRolePrefix = defaultRolePrefix;
    }
}
