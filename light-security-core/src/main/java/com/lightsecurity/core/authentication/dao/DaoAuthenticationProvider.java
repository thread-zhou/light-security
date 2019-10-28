package com.lightsecurity.core.authentication.dao;

import com.lightsecurity.core.authentication.encoding.PasswordEncoder;
import com.lightsecurity.core.authentication.encoding.PlaintextPasswordEncoder;
import com.lightsecurity.core.userdetails.UserDetailsService;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;

public class DaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    //todo

    private PasswordEncoder passwordEncoder;

    private String userNotFoundEncodedPassword;

    private SaltSource saltSource;

    private UserDetailsService userDetailsService;

    public DaoAuthenticationProvider() {
        setPasswordEncoder(new PlaintextPasswordEncoder());
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {

    }
}
