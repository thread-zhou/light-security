package com.lightsecurity.core.filter;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.SpringSecurityMessageSource;
import com.lightsecurity.core.authentication.AuthenticationManager;
import com.lightsecurity.core.context.SecurityContext;
import com.lightsecurity.core.context.SecurityContextHolder;
import com.lightsecurity.core.exception.AccessDeniedException;
import com.lightsecurity.core.exception.AuthenticationCredentialsNotFoundException;
import com.lightsecurity.core.exception.AuthenticationException;
import com.lightsecurity.core.exception.AuthenticationServiceException;
import com.lightsecurity.core.web.access.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.*;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSecurityInterceptor implements InitializingBean,
        ApplicationEventPublisherAware, MessageSourceAware {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private ApplicationEventPublisher eventPublisher;
    private AccessDecisionManager accessDecisionManager;
    private AfterInvocationManager afterInvocationManager;
    private AuthenticationManager authenticationManager = new NoOpAuthenticationManager();
    private RunAsManager runAsManager = new NullRunAsManager();

    private boolean alwaysReauthenticate = false;
    private boolean rejectPublicInvocations = false;
    private boolean validateConfigAttributes = true;
    private boolean publishAuthorizationSuccess = false;


    public void afterPropertiesSet() throws Exception {
        Assert.notNull(getSecureObjectClass(),
                "Subclass must provide a non-null response to getSecureObjectClass()");
        Assert.notNull(this.messages, "A message source must be set");
        Assert.notNull(this.authenticationManager, "An AuthenticationManager is required");
        Assert.notNull(this.accessDecisionManager, "An AccessDecisionManager is required");
        Assert.notNull(this.runAsManager, "A RunAsManager is required");
        Assert.notNull(this.obtainSecurityMetadataSource(),
                "An SecurityMetadataSource is required");
        Assert.isTrue(this.obtainSecurityMetadataSource()
                        .supports(getSecureObjectClass()),
                "SecurityMetadataSource does not support secure object class: "
                        + getSecureObjectClass());
        Assert.isTrue(this.runAsManager.supports(getSecureObjectClass()),
                "RunAsManager does not support secure object class: "
                        + getSecureObjectClass());
        Assert.isTrue(this.accessDecisionManager.supports(getSecureObjectClass()),
                "AccessDecisionManager does not support secure object class: "
                        + getSecureObjectClass());

        if (this.afterInvocationManager != null) {
            Assert.isTrue(this.afterInvocationManager.supports(getSecureObjectClass()),
                    "AfterInvocationManager does not support secure object class: "
                            + getSecureObjectClass());
        }

        if (this.validateConfigAttributes) {
            Collection<ConfigAttribute> attributeDefs = this
                    .obtainSecurityMetadataSource().getAllConfigAttributes();

            if (attributeDefs == null) {
                logger.warn("Could not validate configuration attributes as the SecurityMetadataSource did not return "
                        + "any attributes from getAllConfigAttributes()");
                return;
            }

            Set<ConfigAttribute> unsupportedAttrs = new HashSet<ConfigAttribute>();

            for (ConfigAttribute attr : attributeDefs) {
                if (!this.runAsManager.supports(attr)
                        && !this.accessDecisionManager.supports(attr)
                        && ((this.afterInvocationManager == null) || !this.afterInvocationManager
                        .supports(attr))) {
                    unsupportedAttrs.add(attr);
                }
            }

            if (unsupportedAttrs.size() != 0) {
                throw new IllegalArgumentException(
                        "Unsupported configuration attributes: " + unsupportedAttrs);
            }

            logger.debug("Validated configuration attributes");
        }
    }

    protected InterceptorStatusToken beforeInvocation(Object object) {
        Assert.notNull(object, "Object was null");
        final boolean debug = logger.isDebugEnabled();

        if (!getSecureObjectClass().isAssignableFrom(object.getClass())) {
            throw new IllegalArgumentException(
                    "Security invocation attempted for object "
                            + object.getClass().getName()
                            + " but AbstractSecurityInterceptor only configured to support secure objects of type: "
                            + getSecureObjectClass());
        }

        Collection<ConfigAttribute> attributes = this.obtainSecurityMetadataSource()
                .getAttributes(object);

        if (attributes == null || attributes.isEmpty()) {
            if (rejectPublicInvocations) {
                throw new IllegalArgumentException(
                        "Secure object invocation "
                                + object
                                + " was denied as public invocations are not allowed via this interceptor. "
                                + "This indicates a configuration error because the "
                                + "rejectPublicInvocations property is set to 'true'");
            }

            if (debug) {
                logger.debug("Public object - authentication not attempted");
            }

            publishEvent(new PublicInvocationEvent(object));

            return null; // no further work post-invocation
        }

        if (debug) {
            logger.debug("Secure object: " + object + "; Attributes: " + attributes);
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            credentialsNotFound(messages.getMessage(
                    "AbstractSecurityInterceptor.authenticationNotFound",
                    "An Authentication object was not found in the SecurityContext"),
                    object, attributes);
        }

        Authentication authenticated = authenticateIfRequired();

        // Attempt authorization
        try {
            this.accessDecisionManager.decide(authenticated, object, attributes);
        }
        catch (AccessDeniedException accessDeniedException) {
            publishEvent(new AuthorizationFailureEvent(object, attributes, authenticated,
                    accessDeniedException));

            throw accessDeniedException;
        }

        if (debug) {
            logger.debug("Authorization successful");
        }

        if (publishAuthorizationSuccess) {
            publishEvent(new AuthorizedEvent(object, attributes, authenticated));
        }

        // Attempt to run as a different user
        Authentication runAs = this.runAsManager.buildRunAs(authenticated, object,
                attributes);

        if (runAs == null) {
            if (debug) {
                logger.debug("RunAsManager did not change Authentication object");
            }

            // no further work post-invocation
            return new InterceptorStatusToken(SecurityContextHolder.getContext(), false,
                    attributes, object);
        }
        else {
            if (debug) {
                logger.debug("Switching to RunAs Authentication: " + runAs);
            }

            SecurityContext origCtx = SecurityContextHolder.getContext();
            SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
            SecurityContextHolder.getContext().setAuthentication(runAs);

            // need to revert to token.Authenticated post-invocation
            return new InterceptorStatusToken(origCtx, true, attributes, object);
        }
    }

    protected void finallyInvocation(InterceptorStatusToken token) {
        if (token != null && token.isContextHolderRefreshRequired()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Reverting to original Authentication: "
                        + token.getSecurityContext().getAuthentication());
            }

            SecurityContextHolder.setContext(token.getSecurityContext());
        }
    }

    protected Object afterInvocation(InterceptorStatusToken token, Object returnedObject) {
        if (token == null) {
            // public object
            return returnedObject;
        }

        finallyInvocation(token); // continue to clean in this method for passivity

        if (afterInvocationManager != null) {
            // Attempt after invocation handling
            try {
                returnedObject = afterInvocationManager.decide(token.getSecurityContext()
                        .getAuthentication(), token.getSecureObject(), token
                        .getAttributes(), returnedObject);
            }
            catch (AccessDeniedException accessDeniedException) {
                AuthorizationFailureEvent event = new AuthorizationFailureEvent(
                        token.getSecureObject(), token.getAttributes(), token
                        .getSecurityContext().getAuthentication(),
                        accessDeniedException);
                publishEvent(event);

                throw accessDeniedException;
            }
        }

        return returnedObject;
    }

    private Authentication authenticateIfRequired() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication.isAuthenticated() && !alwaysReauthenticate) {
            if (logger.isDebugEnabled()) {
                logger.debug("Previously Authenticated: " + authentication);
            }

            return authentication;
        }

        authentication = authenticationManager.authenticate(authentication);

        // We don't authenticated.setAuthentication(true), because each provider should do
        // that
        if (logger.isDebugEnabled()) {
            logger.debug("Successfully Authenticated: " + authentication);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

    private void credentialsNotFound(String reason, Object secureObject,
                                     Collection<ConfigAttribute> configAttribs) {
        AuthenticationCredentialsNotFoundException exception = new AuthenticationCredentialsNotFoundException(
                reason);

        AuthenticationCredentialsNotFoundEvent event = new AuthenticationCredentialsNotFoundEvent(
                secureObject, configAttribs, exception);
        publishEvent(event);

        throw exception;
    }

    public abstract SecurityMetadataSource obtainSecurityMetadataSource();

    public abstract Class<?> getSecureObjectClass();

    public AccessDecisionManager getAccessDecisionManager() {
        return accessDecisionManager;
    }

    public AfterInvocationManager getAfterInvocationManager() {
        return afterInvocationManager;
    }

    public AuthenticationManager getAuthenticationManager() {
        return this.authenticationManager;
    }

    public RunAsManager getRunAsManager() {
        return runAsManager;
    }


    public boolean isAlwaysReauthenticate() {
        return alwaysReauthenticate;
    }

    public boolean isRejectPublicInvocations() {
        return rejectPublicInvocations;
    }

    public boolean isValidateConfigAttributes() {
        return validateConfigAttributes;
    }

    public void setAccessDecisionManager(AccessDecisionManager accessDecisionManager) {
        this.accessDecisionManager = accessDecisionManager;
    }

    public void setAfterInvocationManager(AfterInvocationManager afterInvocationManager) {
        this.afterInvocationManager = afterInvocationManager;
    }

    /**
     * Indicates whether the <code>AbstractSecurityInterceptor</code> should ignore the
     * {@link Authentication#isAuthenticated()} property. Defaults to <code>false</code>,
     * meaning by default the <code>Authentication.isAuthenticated()</code> property is
     * trusted and re-authentication will not occur if the principal has already been
     * authenticated.
     *
     * @param alwaysReauthenticate <code>true</code> to force
     * <code>AbstractSecurityInterceptor</code> to disregard the value of
     * <code>Authentication.isAuthenticated()</code> and always re-authenticate the
     * request (defaults to <code>false</code>).
     */
    public void setAlwaysReauthenticate(boolean alwaysReauthenticate) {
        this.alwaysReauthenticate = alwaysReauthenticate;
    }

    public void setApplicationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    public void setAuthenticationManager(AuthenticationManager newManager) {
        this.authenticationManager = newManager;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    /**
     * Only {@code AuthorizationFailureEvent} will be published. If you set this property
     * to {@code true}, {@code AuthorizedEvent}s will also be published.
     *
     * @param publishAuthorizationSuccess default value is {@code false}
     */
    public void setPublishAuthorizationSuccess(boolean publishAuthorizationSuccess) {
        this.publishAuthorizationSuccess = publishAuthorizationSuccess;
    }

    /**
     * By rejecting public invocations (and setting this property to <tt>true</tt>),
     * essentially you are ensuring that every secure object invocation advised by
     * <code>AbstractSecurityInterceptor</code> has a configuration attribute defined.
     * This is useful to ensure a "fail safe" mode where undeclared secure objects will be
     * rejected and configuration omissions detected early. An
     * <tt>IllegalArgumentException</tt> will be thrown by the
     * <tt>AbstractSecurityInterceptor</tt> if you set this property to <tt>true</tt> and
     * an attempt is made to invoke a secure object that has no configuration attributes.
     *
     * @param rejectPublicInvocations set to <code>true</code> to reject invocations of
     * secure objects that have no configuration attributes (by default it is
     * <code>false</code> which treats undeclared secure objects as "public" or
     * unauthorized).
     */
    public void setRejectPublicInvocations(boolean rejectPublicInvocations) {
        this.rejectPublicInvocations = rejectPublicInvocations;
    }

    public void setRunAsManager(RunAsManager runAsManager) {
        this.runAsManager = runAsManager;
    }

    public void setValidateConfigAttributes(boolean validateConfigAttributes) {
        this.validateConfigAttributes = validateConfigAttributes;
    }

    private void publishEvent(ApplicationEvent event) {
        if (this.eventPublisher != null) {
            this.eventPublisher.publishEvent(event);
        }
    }

    private static class NoOpAuthenticationManager implements AuthenticationManager {

        public Authentication authenticate(Authentication authentication)
                throws AuthenticationException {
            throw new AuthenticationServiceException("Cannot authenticate "
                    + authentication);
        }
    }
}
