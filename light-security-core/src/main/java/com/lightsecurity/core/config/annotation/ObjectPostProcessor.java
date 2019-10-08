package com.lightsecurity.core.config.annotation;

/**
 *
 * @param <T>
 */
public interface ObjectPostProcessor <T> {

    <O extends T> O postProcess(O object);

}
