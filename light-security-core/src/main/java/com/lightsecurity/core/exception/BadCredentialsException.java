package com.lightsecurity.core.exception;

/**
 * 如果由于凭据无效而拒绝身份验证请求, 则抛出该异常
 */
public class BadCredentialsException extends AuthenticationException {

    public BadCredentialsException(String msg){
        super(msg);
    }

    public BadCredentialsException(String msg, Throwable t) {
        super(msg, t);
    }

}
