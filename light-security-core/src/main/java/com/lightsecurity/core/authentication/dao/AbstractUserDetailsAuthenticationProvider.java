package com.lightsecurity.core.authentication.dao;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.LightSecurityMessageSource;
import com.lightsecurity.core.authentication.AuthenticationProvider;
import com.lightsecurity.core.authentication.UsernamePasswordAuthenticationToken;
import com.lightsecurity.core.exception.*;
import com.lightsecurity.core.userdetails.UserDetails;
import com.lightsecurity.core.userdetails.UserDetailsChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;


public abstract class AbstractUserDetailsAuthenticationProvider implements
        AuthenticationProvider, InitializingBean, MessageSourceAware {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks();
    private UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();

    protected MessageSourceAccessor messages = LightSecurityMessageSource.getAccessor();
    //todo


    @Override
    public boolean support(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
                messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.onlySupports",
                        "Only UsernamePasswordAuthenticationToken is supported"));

        String username = (authentication.getPrincipal() == null) ? "" : authentication.getName();

        UserDetails user = null;
        try {
            user = retrieveUser(username, (UsernamePasswordAuthenticationToken) authentication);
        }catch (UsernameNotFoundException notFound){
            logger.debug("User '" + username + "' not found");
            throw notFound;
        }
        Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");
        
        try {
            preAuthenticationChecks.check(user);
            additionalAuthenticationChecks(user, (UsernamePasswordAuthenticationToken) authentication);
        }catch (AuthenticationException exception){
            // TODO: 2019/10/29 缓存问题处理 
            logger.warn("未处理异常: {}", exception.getMessage());
            throw exception;
        }
        postAuthenticationChecks.check(user);

        Object principalToReturn = user;
        return createSuccessAuthentication(principalToReturn, authentication, user);
    }

    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user){
        // TODO: 2019/10/29 加载权限
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                principal, authentication.getCredentials(),null);
        result.setDetails(authentication.getDetails());
        return result;
    }

    /**
     * 密码校验
     * @param user
     * @param authentication
     * @throws AuthenticationException
     */
    protected abstract void additionalAuthenticationChecks(UserDetails user, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException;

    /**
     * 真正的认证获取主体的方法实现
     * @param username
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    protected abstract UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException;

    private class DefaultPreAuthenticationChecks implements UserDetailsChecker {
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                logger.debug("User account is locked");

                throw new LockedException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.locked",
                        "User account is locked"));
            }

            if (!user.isEnabled()) {
                logger.debug("User account is disabled");

                throw new DisabledException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.disabled",
                        "User is disabled"));
            }

            if (!user.isAccountNonExpired()) {
                logger.debug("User account is expired");

                throw new AccountExpiredException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.expired",
                        "User account has expired"));
            }
        }
    }

    private class DefaultPostAuthenticationChecks implements UserDetailsChecker {
        public void check(UserDetails user) {
            if (!user.isCredentialsNonExpired()) {
                logger.debug("User account credentials have expired");

                throw new CredentialsExpiredException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.credentialsExpired",
                        "User credentials have expired"));
            }
        }
    }
}
