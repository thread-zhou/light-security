package com.lightsecurity.core.config;

import com.lightsecurity.core.authentication.AuthenticationManager;
import com.lightsecurity.core.authority.AuthenticationManagerBuilder;
import com.lightsecurity.core.config.annotation.SecurityConfigurer;
import org.springframework.core.annotation.Order;

/**
 * ClassName GlobalAuthenticationConfigurerAdapter
 * Description TODO
 * Author Administrator
 * Date 2019/10/9 16:10
 **/
@Order(100)
public abstract class GlobalAuthenticationConfigurerAdapter implements SecurityConfigurer<AuthenticationManager, AuthenticationManagerBuilder> {

    public void init(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception{}

    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception{}
}
