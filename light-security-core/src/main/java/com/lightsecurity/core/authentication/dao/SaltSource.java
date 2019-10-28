package com.lightsecurity.core.authentication.dao;

import com.lightsecurity.core.userdetails.UserDetails;

public interface SaltSource {
    // ~ Methods
    // ========================================================================================================

    /**
     * Returns the salt to use for the indicated user.
     *
     * @param user from the <code>AuthenticationDao</code>
     *
     * @return the salt to use for this <code>UserDetails</code>
     */
    Object getSalt(UserDetails user);
}

