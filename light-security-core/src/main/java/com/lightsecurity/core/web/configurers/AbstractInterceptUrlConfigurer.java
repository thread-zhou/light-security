package com.lightsecurity.core.web.configurers;

import com.lightsecurity.core.authentication.AuthenticationManager;
import com.lightsecurity.core.config.annotation.web.HttpSecurityBuilder;
import com.lightsecurity.core.filter.FilterSecurityInterceptor;
import com.lightsecurity.core.web.access.AbstractConfigAttributeRequestMatcherRegistry;
import com.lightsecurity.core.web.access.AccessDecisionManager;
import com.lightsecurity.core.web.access.FilterInvocationSecurityMetadataSource;
import com.lightsecurity.core.web.access.vote.AccessDecisionVoter;
import com.lightsecurity.core.web.access.vote.AffirmativeBased;

import java.util.List;

abstract class AbstractInterceptUrlConfigurer<C extends AbstractInterceptUrlConfigurer<C, H>, H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<C, H> {
    private Boolean filterSecurityInterceptorOncePerRequest;

    private AccessDecisionManager accessDecisionManager;

    @Override
    public void configure(H http) throws Exception {
        FilterInvocationSecurityMetadataSource metadataSource = createMetadataSource(http);
        if (metadataSource == null) {
            return;
        }
        FilterSecurityInterceptor securityInterceptor = createFilterSecurityInterceptor(
                http, metadataSource, http.getSharedObject(AuthenticationManager.class));
        if (filterSecurityInterceptorOncePerRequest != null) {
            securityInterceptor
                    .setObserveOncePerRequest(filterSecurityInterceptorOncePerRequest);
        }
        securityInterceptor = postProcess(securityInterceptor);
        http.addFilter(securityInterceptor);
        http.setSharedObject(FilterSecurityInterceptor.class, securityInterceptor);
    }

    /**
     * Subclasses should implement this method to provide a
     * {@link FilterInvocationSecurityMetadataSource} for the
     * {@link FilterSecurityInterceptor}.
     *
     * @param http the builder to use
     *
     * @return the {@link FilterInvocationSecurityMetadataSource} to set on the
     * {@link FilterSecurityInterceptor}. Cannot be null.
     */
    abstract FilterInvocationSecurityMetadataSource createMetadataSource(H http);

    /**
     * Subclasses should implement this method to provide the {@link AccessDecisionVoter}
     * instances used to create the default {@link AccessDecisionManager}
     *
     * @param http the builder to use
     *
     * @return the {@link AccessDecisionVoter} instances used to create the default
     * {@link AccessDecisionManager}
     */
    abstract List<AccessDecisionVoter<? extends Object>> getDecisionVoters(H http);

    abstract class AbstractInterceptUrlRegistry<R extends AbstractInterceptUrlRegistry<R, T>, T>
            extends AbstractConfigAttributeRequestMatcherRegistry<T> {

        /**
         * Allows setting the {@link AccessDecisionManager}. If none is provided, a
         * default {@link AccessDecisionManager} is created.
         *
         * @param accessDecisionManager the {@link AccessDecisionManager} to use
         * @return the {@link AbstractInterceptUrlConfigurer} for further customization
         */
        public R accessDecisionManager(AccessDecisionManager accessDecisionManager) {
            AbstractInterceptUrlConfigurer.this.accessDecisionManager = accessDecisionManager;
            return getSelf();
        }

        /**
         * Allows setting if the {@link FilterSecurityInterceptor} should be only applied
         * once per request (i.e. if the filter intercepts on a forward, should it be
         * applied again).
         *
         * @param filterSecurityInterceptorOncePerRequest if the
         * {@link FilterSecurityInterceptor} should be only applied once per request
         * @return the {@link AbstractInterceptUrlConfigurer} for further customization
         */
        public R filterSecurityInterceptorOncePerRequest(
                boolean filterSecurityInterceptorOncePerRequest) {
            AbstractInterceptUrlConfigurer.this.filterSecurityInterceptorOncePerRequest = filterSecurityInterceptorOncePerRequest;
            return getSelf();
        }

        /**
         * Returns a reference to the current object with a single suppression of the type
         *
         * @return a reference to the current object
         */
        @SuppressWarnings("unchecked")
        private R getSelf() {
            return (R) this;
        }
    }

    /**
     * Creates the default {@code AccessDecisionManager}
     * @return the default {@code AccessDecisionManager}
     */
    private AccessDecisionManager createDefaultAccessDecisionManager(H http) {
        AffirmativeBased result = new AffirmativeBased(getDecisionVoters(http));
        return postProcess(result);
    }

    /**
     * If currently null, creates a default {@link AccessDecisionManager} using
     * {@link #createDefaultAccessDecisionManager(HttpSecurityBuilder)}. Otherwise returns the
     * {@link AccessDecisionManager}.
     *
     * @param http the builder to use
     *
     * @return the {@link AccessDecisionManager} to use
     */
    private AccessDecisionManager getAccessDecisionManager(H http) {
        if (accessDecisionManager == null) {
            accessDecisionManager = createDefaultAccessDecisionManager(http);
        }
        return accessDecisionManager;
    }

    /**
     * Creates the {@link FilterSecurityInterceptor}
     *
     * @param http the builder to use
     * @param metadataSource the {@link FilterInvocationSecurityMetadataSource} to use
     * @param authenticationManager the {@link AuthenticationManager} to use
     * @return the {@link FilterSecurityInterceptor}
     * @throws Exception
     */
    private FilterSecurityInterceptor createFilterSecurityInterceptor(H http,
                                                                      FilterInvocationSecurityMetadataSource metadataSource,
                                                                      AuthenticationManager authenticationManager) throws Exception {
        FilterSecurityInterceptor securityInterceptor = new FilterSecurityInterceptor();
        securityInterceptor.setSecurityMetadataSource(metadataSource);
        securityInterceptor.setAccessDecisionManager(getAccessDecisionManager(http));
        securityInterceptor.setAuthenticationManager(authenticationManager);
        securityInterceptor.afterPropertiesSet();
        return securityInterceptor;
    }
}
