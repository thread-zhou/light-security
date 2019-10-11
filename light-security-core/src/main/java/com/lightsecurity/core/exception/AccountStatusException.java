package com.lightsecurity.core.exception;

/**
 * 由特定用户状态(锁定, 禁用等)引起的身份验证异常基类
 */
public abstract class AccountStatusException extends AuthenticationException {
    public AccountStatusException(String msg) {
        super(msg);
    }

    public AccountStatusException(String msg, Throwable t) {
        super(msg, t);
    }
}
