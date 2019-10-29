package com.lightsecurity.core.exception;

public class DisabledException extends AccountStatusException {
    // ~ Constructors
    // ===================================================================================================

    /**
     * Constructs a <code>DisabledException</code> with the specified message.
     *
     * @param msg the detail message
     */
    public DisabledException(String msg) {
        super(msg);
    }

    /**
     * Constructs a <code>DisabledException</code> with the specified message and root
     * cause.
     *
     * @param msg the detail message
     * @param t root cause
     */
    public DisabledException(String msg, Throwable t) {
        super(msg, t);
    }
}