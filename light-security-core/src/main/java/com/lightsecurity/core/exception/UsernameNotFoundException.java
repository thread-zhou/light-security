package com.lightsecurity.core.exception;

/**
 * 如果{@link com.lightsecurity.core.userdetails.UserDetailsService}实现无法通过username定位一个User对象, 则抛出该异常
 */
public class UsernameNotFoundException extends AuthenticationException {
    // ~ Constructors
    // ===================================================================================================

    /**
     * Constructs a <code>UsernameNotFoundException</code> with the specified message.
     *
     * @param msg the detail message.
     */
    public UsernameNotFoundException(String msg) {
        super(msg);
    }

    /**
     * Constructs a {@code UsernameNotFoundException} with the specified message and root
     * cause.
     *
     * @param msg the detail message.
     * @param t root cause
     */
    public UsernameNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}
