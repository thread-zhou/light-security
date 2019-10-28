package com.lightsecurity.core.authentication;

public interface AuthenticationDetailsSource<C, T> {

    T bindDetails(C context);

}
