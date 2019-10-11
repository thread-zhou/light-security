package com.lightsecurity.core.authority;

import com.lightsecurity.core.authentication.AuthenticationEventPublisher;
import com.lightsecurity.core.authentication.AuthenticationManager;
import com.lightsecurity.core.authentication.ProviderManager;
import com.lightsecurity.core.authentication.ProviderManagerBuilder;
import com.lightsecurity.core.config.annotation.AbstractConfiguredSecurityBuilder;
import com.lightsecurity.core.config.annotation.ObjectPostProcessor;
import com.lightsecurity.core.userdetails.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class AuthenticationManagerBuilder extends AbstractConfiguredSecurityBuilder<AuthenticationManager, AuthenticationManagerBuilder> implements ProviderManagerBuilder<AuthenticationManagerBuilder> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private AuthenticationManager parentAuthenticationManager;

    private AuthenticationEventPublisher eventPublisher;

    private Boolean eraseCredentials;

    private UserDetailsService defaultUserDetailsService;

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

    public UserDetailsService getDefaultUserDetailsService() {
        return this.defaultUserDetailsService;
    }

    @Override
    protected AuthenticationManager performBuild() throws Exception {
        //todo
        return null;
    }

    @Override
    public AuthenticationManagerBuilder authenticationProvider(AuthenticationProvider authenticationProvider) {
        return null;
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
