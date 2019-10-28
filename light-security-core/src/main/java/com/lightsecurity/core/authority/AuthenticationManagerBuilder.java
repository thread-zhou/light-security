package com.lightsecurity.core.authority;

import com.lightsecurity.core.authentication.*;
import com.lightsecurity.core.config.annotation.AbstractConfiguredSecurityBuilder;
import com.lightsecurity.core.config.annotation.ObjectPostProcessor;
import com.lightsecurity.core.userdetails.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationManagerBuilder extends AbstractConfiguredSecurityBuilder<AuthenticationManager, AuthenticationManagerBuilder> implements ProviderManagerBuilder<AuthenticationManagerBuilder> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private AuthenticationManager parentAuthenticationManager;

    private AuthenticationEventPublisher eventPublisher;

    private Boolean eraseCredentials;

    private UserDetailsService defaultUserDetailsService;

    private List<com.lightsecurity.core.authentication.AuthenticationProvider> authenticationProviders = new ArrayList<>();

    public AuthenticationManagerBuilder(ObjectPostProcessor<Object> objectPostProcessor) {
        super(objectPostProcessor);
    }

    public AuthenticationManagerBuilder parentAuthenticationManager(AuthenticationManager authenticationManager){
        if (authenticationManager instanceof ProviderManager){
            eraseCredentials(((ProviderManager) authenticationManager).isEraseCredentialsAfterAuthentication());
        }
        this.parentAuthenticationManager = authenticationManager;
        return this;
    }

    public boolean isConfigured(){
        return !authenticationProviders.isEmpty() || parentAuthenticationManager != null;
    }

    public UserDetailsService getDefaultUserDetailsService() {
        return this.defaultUserDetailsService;
    }

    @Override
    public AuthenticationManagerBuilder authenticationProvider(AuthenticationProvider authenticationProvider){
        this.authenticationProviders.add(authenticationProvider);
        return this;
    }

    @Override
    protected AuthenticationManager performBuild() throws Exception {
        //todo
        if (!isConfigured()){
            logger.debug("No authenticationProviders and no parentAuthenticationManager defined. Returning null.");
            return null;
        }
        ProviderManager providerManager = new ProviderManager(authenticationProviders, parentAuthenticationManager);
        if (eraseCredentials != null){
            providerManager.setEraseCredentialsAfterAuthentication(eraseCredentials);
        }
        if (eventPublisher != null){
            providerManager.setEventPublisher(eventPublisher);
        }
        providerManager = postProcess(providerManager);
        return providerManager;
    }


    //todo 完成AuthenticationManagerBuilder的编写

    /**
     * 注入事件发布实例
     * @param eventPublisher
     * @return
     */
    public AuthenticationManagerBuilder authenticationEventPublisher(AuthenticationEventPublisher eventPublisher){
        Assert.notNull(eventPublisher, "AuthenticationEventPublisher cannot be null");
        this.eventPublisher = eventPublisher;
        return this;
    }

    /**
     * 如果eraseCredentials为true, {@link AuthenticationManager}应该清除验证后来自{@link com.lightsecurity.core.Authentication}对象的凭据
     *
     * @param eraseCredentials
     * @return 返回{@link AuthenticationMangerBuilder}进行进一步的自定义
     */
    public AuthenticationManagerBuilder eraseCredentials(boolean eraseCredentials){
        this.eraseCredentials = eraseCredentials;
        return this;
    }
}
