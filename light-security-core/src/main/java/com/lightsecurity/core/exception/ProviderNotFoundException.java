package com.lightsecurity.core.exception;

/**
 * 如果找不到{@link com.lightsecurity.core.authentication.AuthenticationProvider}
 * , 则由{@link com.lightsecurity.core.authentication.ProviderManager}抛出
 */
public class ProviderNotFoundException extends AuthenticationException {

    public ProviderNotFoundException(String msg) {
        super(msg);
    }
}
