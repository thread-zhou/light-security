package com.lightsecurity.core.context;

import com.lightsecurity.core.Authentication;

import java.io.Serializable;

public interface SecurityContext extends Serializable {

    Authentication getAuthentication();


    void setAuthentication(Authentication authentication);

}
