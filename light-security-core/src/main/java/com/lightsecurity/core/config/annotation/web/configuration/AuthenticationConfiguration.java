package com.lightsecurity.core.config.annotation.web.configuration;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.authentication.AuthenticationManager;
import com.lightsecurity.core.authority.AuthenticationManagerBuilder;
import com.lightsecurity.core.config.GlobalAuthenticationConfigurerAdapter;
import com.lightsecurity.core.config.annotation.ObjectPostProcessor;
import com.lightsecurity.core.exception.AuthenticationException;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Import({ObjectPostProcessorConfiguration.class})
@Configuration
public class AuthenticationConfiguration {
    //todo 后面增加

    private AtomicBoolean buildingAuthenticationManager = new AtomicBoolean();

    private ApplicationContext applicationContext;

    private List<GlobalAuthenticationConfigurerAdapter> globalAuthConfigurers = Collections.emptyList();

    private AuthenticationManager authenticationManager;

    private ObjectPostProcessor<Object> objectPostProcessor;

    private boolean authenticationManagerInitialized;

    @Bean
    public AuthenticationManagerBuilder authenticationManagerBuilder(ObjectPostProcessor<Object> objectPostProcessor){
        return new AuthenticationManagerBuilder(objectPostProcessor);
    }


    public AuthenticationManager getAuthenticationManager() throws Exception{
        //todo 回顾本方法流程并理解
        if (this.authenticationManagerInitialized){
            return this.authenticationManager;
        }

        AuthenticationManagerBuilder authenticationManagerBuilder = authenticationManagerBuilder(this.objectPostProcessor);

        if (this.buildingAuthenticationManager.getAndSet(true)){
            return new AuthenticationManagerDelegator(authenticationManagerBuilder);
        }
        for (GlobalAuthenticationConfigurerAdapter config : globalAuthConfigurers){
            authenticationManagerBuilder.apply(config);//添加配置器
        }
        authenticationManager = authenticationManagerBuilder.build();
        if (authenticationManager == null){
            authenticationManager = getAuthenticationManagerBean();
        }
        this.authenticationManagerInitialized = true;
        return authenticationManager;
    }

    private <T> T lazyBean(Class<T> interfaceName){
        LazyInitTargetSource lazyInitTargetSource = new LazyInitTargetSource();
        String[] beanNamesForType = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext, interfaceName);
        if (beanNamesForType.length == 0) {
            return null;
        }
        Assert.isTrue(beanNamesForType.length == 1,
                "Expecting to only find a single bean for type " + interfaceName
                        + ", but found " + Arrays.asList(beanNamesForType));
        lazyInitTargetSource.setTargetBeanName(beanNamesForType[0]);
        lazyInitTargetSource.setBeanFactory(applicationContext);
        ProxyFactoryBean proxyFactory = new ProxyFactoryBean();
        proxyFactory = objectPostProcessor.postProcess(proxyFactory);
        proxyFactory.setTargetSource(lazyInitTargetSource);
        return (T) proxyFactory.getObject();
    }

    private AuthenticationManager getAuthenticationManagerBean(){
        return lazyBean(AuthenticationManager.class);
    }

    /**
     * Prevents infinite recursion in the event that initializing the
     * AuthenticationManager.
     *
     * @author Rob Winch
     * @since 4.1.1
     */
    static final class AuthenticationManagerDelegator implements AuthenticationManager {
        private AuthenticationManagerBuilder delegateBuilder;
        private AuthenticationManager delegate;
        private final Object delegateMonitor = new Object();

        AuthenticationManagerDelegator(AuthenticationManagerBuilder delegateBuilder) {
            Assert.notNull(delegateBuilder, "delegateBuilder cannot be null");
            this.delegateBuilder = delegateBuilder;
        }

        @Override
        public Authentication authenticate(Authentication authentication)
                throws AuthenticationException {
            if (this.delegate != null) {
                return this.delegate.authenticate(authentication);
            }

            synchronized (this.delegateMonitor) {
                if (this.delegate == null) {
                    this.delegate = this.delegateBuilder.getObject();
                    this.delegateBuilder = null;
                }
            }

            return this.delegate.authenticate(authentication);
        }

        @Override
        public String toString() {
            return "AuthenticationManagerDelegator [delegate=" + this.delegate + "]";
        }
    }

}
