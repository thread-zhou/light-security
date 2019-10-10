package com.lightsecurity.core.authentication;

import com.lightsecurity.core.authority.AuthenticationProvider;
import com.lightsecurity.core.config.annotation.SecurityBuilder;

/**
 * InterfaceName ProviderManagerBuilder
 * Description TODO
 * Author Administrator
 * Date 2019/10/9 16:00
 **/
public interface ProviderManagerBuilder<B extends ProviderManagerBuilder<B>> extends SecurityBuilder<AuthenticationManager> {


    B authenticationProvider(AuthenticationProvider authenticationProvider);

}
