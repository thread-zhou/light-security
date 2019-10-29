package com.lightsecurity.core.exception;

public class LockedException extends AccountStatusException {
    // ~ Constructors
    // ===================================================================================================

    /**
     * Constructs a <code>LockedException</code> with the specified message.
     *
     * @param msg the detail message.
     */
    public LockedException(String msg) {
        super(msg);
    }

    /**
     * Constructs a <code>LockedException</code> with the specified message and root
     * cause.
     *
     * @param msg the detail message.
     * @param t root cause
     */
    public LockedException(String msg, Throwable t) {
        super(msg, t);
    }
}
