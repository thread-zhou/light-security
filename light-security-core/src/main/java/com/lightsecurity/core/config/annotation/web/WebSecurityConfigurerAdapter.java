package com.lightsecurity.core.config.annotation.web;

import com.lightsecurity.core.authentication.AuthenticationManager;
import com.lightsecurity.core.authority.AuthenticationManagerBuilder;
import com.lightsecurity.core.config.annotation.ObjectPostProcessor;
import com.lightsecurity.core.config.annotation.web.configuration.AuthenticationConfiguration;
import com.lightsecurity.core.web.builders.HttpSecurity;
import com.lightsecurity.core.web.builders.WebSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;

@Order(100)
public abstract class WebSecurityConfigurerAdapter implements WebSecurityConfigurer<WebSecurity> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext context;

    private boolean disableDefaults;

    private AuthenticationManager authenticationManager;

    private AuthenticationConfiguration authenticationConfiguration;

    private AuthenticationManagerBuilder authenticationManagerBuilder;
    private AuthenticationManagerBuilder localConfigureAuthenticationBuilder;


    private HttpSecurity httpSecurity;

    private boolean authenticationManagerInitialized;

    private boolean disableLocalConfigureAuthenticationBuilder;

    private ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<Object>() {
        @Override
        public <O> O postProcess(O object) {
            throw new IllegalStateException(
                    ObjectPostProcessor.class.getName()
                            + " is a required bean. Ensure you have used @EnableWebSecurity and @Configuration");
        }
    };

    protected WebSecurityConfigurerAdapter() {
        this(false);
    }

    protected WebSecurityConfigurerAdapter(boolean disableDefaults) {
        this.disableDefaults = disableDefaults;
    }


    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        this.disableLocalConfigureAuthenticationBuilder = true;
    }


    protected final HttpSecurity getHttpSecurity() throws Exception{
        if (httpSecurity != null){
            return httpSecurity;
        }
        //todo 完成httpSecurity的创建
        return null;
    }

    protected AuthenticationManager authenticationManager() throws Exception{
        //todo 回顾本方法流程，并理解内容
        if (!authenticationManagerInitialized){
            configure(localConfigureAuthenticationBuilder);
            if (disableLocalConfigureAuthenticationBuilder){
                authenticationManager = authenticationConfiguration.getAuthenticationManager();
            }else {
                authenticationManager = localConfigureAuthenticationBuilder.build();
            }
            authenticationManagerInitialized = true;
        }
        return authenticationManager;
    }



    @Override
    public void init(WebSecurity builder) throws Exception {
        //todo 完成init方法
        logger.info("hello configurer init");
    }

    @Override
    public void configure(WebSecurity builder) throws Exception {

    }

    protected void configure(HttpSecurity http) throws Exception {

    }
}
