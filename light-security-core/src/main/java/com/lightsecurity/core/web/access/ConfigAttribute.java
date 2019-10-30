package com.lightsecurity.core.web.access;

import java.io.Serializable;

public interface ConfigAttribute extends Serializable {
    // ~ Methods
    // ========================================================================================================

    /**
     * If the <code>ConfigAttribute</code> can be represented as a <code>String</code> and
     * that <code>String</code> is sufficient in precision to be relied upon as a
     * configuration parameter by a {@link RunAsManager}, {@link AccessDecisionManager} or
     * <code>AccessDecisionManager</code> delegate, this method should return such a
     * <code>String</code>.
     * <p>
     * If the <code>ConfigAttribute</code> cannot be expressed with sufficient precision
     * as a <code>String</code>, <code>null</code> should be returned. Returning
     * <code>null</code> will require any relying classes to specifically support the
     * <code>ConfigAttribute</code> implementation, so returning <code>null</code> should
     * be avoided unless actually required.
     *
     * @return a representation of the configuration attribute (or <code>null</code> if
     * the configuration attribute cannot be expressed as a <code>String</code> with
     * sufficient precision).
     */
    String getAttribute();
}
