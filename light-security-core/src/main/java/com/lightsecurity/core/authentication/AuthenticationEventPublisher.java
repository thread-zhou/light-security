package com.lightsecurity.core.authentication;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.exception.AuthenticationException;

/**
 * 身份验证应用程序事件接口
 */
public interface AuthenticationEventPublisher {

    /**
     * 身份认证成功事件发布方法
     * @param authentication
     */
    void publishAuthenticationSuccess(Authentication authentication);

    /**
     * 身份认证失败事件发布方法
     * @param exception
     * @param authentication
     */
    void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication);

}
