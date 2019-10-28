package com.lightsecurity.core.authentication;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.CredentialsContainer;
import com.lightsecurity.core.LightSecurityMessageSource;
import com.lightsecurity.core.exception.AccountStatusException;
import com.lightsecurity.core.exception.AuthenticationException;
import com.lightsecurity.core.exception.InternalAuthenticationServiceException;
import com.lightsecurity.core.exception.ProviderNotFoundException;
import javafx.scene.effect.Bloom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

public class ProviderManager implements AuthenticationManager, MessageSourceAware, InitializingBean {


    private static final Logger logger = LoggerFactory.getLogger(ProviderManager.class);

    //是否在认证之后擦除凭证信息
    private boolean eraseCredentialsAfterAuthentication = true;
    private AuthenticationEventPublisher eventPublisher = new NullEventPublisher();

    private List<AuthenticationProvider> providers = Collections.emptyList();

    private AuthenticationManager parent;

    private MessageSourceAccessor messages = LightSecurityMessageSource.getAccessor();

    public ProviderManager(List<AuthenticationProvider> providers){
        this(providers, null);
    }

    public ProviderManager(List<AuthenticationProvider> providers, AuthenticationManager parent){
        Assert.notNull(providers, "providers list cannot be null");
        this.providers = providers;
        this.parent = parent;
        checkState();
    }

    private void checkState() {
        if (parent == null && providers.isEmpty()) {
            throw new IllegalArgumentException(
                    "A parent AuthenticationManager or a list "
                            + "of AuthenticationProviders is required");
        }
    }

    public boolean isEraseCredentialsAfterAuthentication() {
        return eraseCredentialsAfterAuthentication;
    }

    public void setEraseCredentialsAfterAuthentication(boolean eraseCredentialsAfterAuthentication) {
        this.eraseCredentialsAfterAuthentication = eraseCredentialsAfterAuthentication;
    }

    public void setEventPublisher(AuthenticationEventPublisher eventPublisher) {
        Assert.notNull(eventPublisher, "AuthenticationEventPublisher cannot be null");
        this.eventPublisher = eventPublisher;
    }

    //todo

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Class<? extends Authentication> originClass = authentication.getClass();
        AuthenticationException lastException = null;
        Authentication result = null;
        boolean debug = logger.isDebugEnabled();

        for (AuthenticationProvider provider : providers){
            if (!provider.support(originClass)){
                continue;
            }
            if (debug){
                logger.debug("Authentication attempt using "
                        + provider.getClass().getName());
            }
            try {
                result = provider.authenticate(authentication);

                if (result != null){
                    copyDetail(authentication, result);
                }
            }catch (AccountStatusException e){
                prepareException(e, authentication);
                // SEC-546: Avoid polling additional providers if auth failure is due to
                // invalid account status
                throw e;
            }catch (InternalAuthenticationServiceException e) {
                prepareException(e, authentication);
                throw e;
            }
            catch (AuthenticationException e) {
                lastException = e;
            }
        }

        if (result == null && parent != null){
            try {
                result = parent.authenticate(authentication);
            }catch (ProviderNotFoundException e){
                // ignore as we will throw below if no other exception occurred prior to
                // calling parent and the parent
                // may throw ProviderNotFound even though a provider in the child already
                // handled the request
            }catch (AuthenticationException e){
                lastException = e;
            }
        }
        if (result != null){
            if (eraseCredentialsAfterAuthentication && (result instanceof CredentialsContainer)){
                ((CredentialsContainer) result).eraseCredentials();
            }
            eventPublisher.publishAuthenticationSuccess(result);
            return result;
        }

        // Parent was null, or didn't authenticate (or throw an exception).
        if (lastException == null){
            lastException = new ProviderNotFoundException(messages.getMessage(
                    "ProviderManager.providerNotFound",
                    new Object[] { originClass.getName() },
                    "No AuthenticationProvider found for {0}"));
        }
        prepareException(lastException, authentication);
        throw  lastException;
    }

    private void prepareException(AuthenticationException e, Authentication authentication) {
        eventPublisher.publishAuthenticationFailure(e, authentication);
    }

    private void copyDetail(Authentication authentication, Authentication result) {
        if ((result instanceof AbstractAuthenticationToken) && (result.getDetails() == null)){
            AbstractAuthenticationToken token = (AbstractAuthenticationToken) result;
            token.setDetails(authentication.getDetails());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        checkState();
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {

    }



    private static final class NullEventPublisher implements AuthenticationEventPublisher {
        public void publishAuthenticationFailure(AuthenticationException exception,
                                                 Authentication authentication) {
        }

        public void publishAuthenticationSuccess(Authentication authentication) {
        }
    }
}
