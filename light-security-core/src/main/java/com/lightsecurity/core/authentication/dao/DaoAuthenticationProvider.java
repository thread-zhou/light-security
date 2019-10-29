package com.lightsecurity.core.authentication.dao;

import com.lightsecurity.core.authentication.UsernamePasswordAuthenticationToken;
import com.lightsecurity.core.authentication.encoding.PlaintextPasswordEncoder;
import com.lightsecurity.core.exception.AuthenticationException;
import com.lightsecurity.core.exception.BadCredentialsException;
import com.lightsecurity.core.exception.InternalAuthenticationServiceException;
import com.lightsecurity.core.exception.UsernameNotFoundException;
import com.lightsecurity.core.userdetails.UserDetails;
import com.lightsecurity.core.userdetails.UserDetailsService;
import com.lightsecurity.core.util.BCryptPasswordEncoder;
import com.lightsecurity.core.util.PasswordEncoder;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;

public class DaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    //todo

    private PasswordEncoder passwordEncoder;

    private String userNotFoundEncodedPassword;

    private SaltSource saltSource;

    private UserDetailsService userDetailsService;

    public DaoAuthenticationProvider() {}

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    @Override
    protected final UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        UserDetails loadedUser;
        try {
            loadedUser = this.getUserDetailsService().loadUserByUsername(username);
        }catch (UsernameNotFoundException notFound){
            throw notFound;
        }catch (Exception repositoryProblem){
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
        }
        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException(
                    "UserDetailsService returned null, which is an interface contract violation");
        }
        return loadedUser;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails details, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        Object salt = null;
        if (this.saltSource != null){
            salt = this.saltSource.getSalt(details);
        }
        //密码加解密暂时没有使用到salt

        if (authentication.getCredential() == null){
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
        String presentedPassword = authentication.getCredentials().toString();

        //presentedPassword 前端传值       details.getPassword()数据库密码, 存储的是经过encode后的值
        if (!passwordEncoder.matches(presentedPassword, details.getPassword())) {
            logger.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {

    }
}
