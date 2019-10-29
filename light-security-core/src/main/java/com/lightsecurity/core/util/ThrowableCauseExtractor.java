package com.lightsecurity.core.util;

public interface ThrowableCauseExtractor {

    Throwable extractCause(Throwable throwable);

}
