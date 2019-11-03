package com.lightsecurity.core.web.access;

import com.lightsecurity.core.Authentication;

public interface SecurityExpressionOperations {

    /**
     * Gets the {@link Authentication} used for evaluating the expressions
     * @return the {@link Authentication} for evaluating the expressions
     */
    Authentication getAuthentication();


    boolean hasAuthority(String authority);


    boolean hasAnyAuthority(String... authorities);


    boolean hasRole(String role);

    boolean hasAnyRole(String... roles);

    /**
     * Always grants access.
     * @return true
     */
    boolean permitAll();

    /**
     * Always denies access
     * @return false
     */
    boolean denyAll();

    /**
     * Determines if the {@link #getAuthentication()} is anonymous
     * @return true if the user is anonymous, else false
     */
    boolean isAnonymous();

    /**
     * Determines ifthe {@link #getAuthentication()} is authenticated
     * @return true if the {@link #getAuthentication()} is authenticated, else false
     */
    boolean isAuthenticated();

    /**
     * Determines if the {@link #getAuthentication()} was authenticated using remember me
     * @return true if the {@link #getAuthentication()} authenticated using remember me,
     * else false
     */
    boolean isRememberMe();

    /**
     * Determines if the {@link #getAuthentication()} authenticated without the use of
     * remember me
     * @return true if the {@link #getAuthentication()} authenticated without the use of
     * remember me, else false
     */
    boolean isFullyAuthenticated();

    /**
     * Determines if the {@link #getAuthentication()} has permission to access the target
     * given the permission
     * @param target the target domain object to check permission on
     * @param permission the permission to check on the domain object (i.e. "read",
     * "write", etc).
     * @return true if permission is granted to the {@link #getAuthentication()}, else
     * false
     */
    boolean hasPermission(Object target, Object permission);

    /**
     * Determines if the {@link #getAuthentication()} has permission to access the domain
     * object with a given id, type, and permission.
     * @param targetId the identifier of the domain object to determine access
     * @param targetType the type (i.e. com.example.domain.Message)
     * @param permission the perission to check on the domain object (i.e. "read",
     * "write", etc)
     * @return true if permission is granted to the {@link #getAuthentication()}, else
     * false
     */
    boolean hasPermission(Object targetId, String targetType, Object permission);

}
