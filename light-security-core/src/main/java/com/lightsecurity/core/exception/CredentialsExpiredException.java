package com.lightsecurity.core.exception;

/**
 * 由于账户凭据已过期而拒绝身份验证请求, 则抛出该异常
 */
public class CredentialsExpiredException extends AccountStatusException {
    public CredentialsExpiredException(String msg) {
        super(msg);
    }

    public CredentialsExpiredException(String msg, Throwable t) {
        super(msg, t);
    }
}
