package com.lightsecurity.core.config.annotation;

public class AlreadyBuiltException extends IllegalStateException {

    public AlreadyBuiltException(String message){
        super(message);
    }

}
