package com.lightsecurity.core.authority;


import com.lightsecurity.core.DefaultPermission;
import com.lightsecurity.core.GrantedAuthority;
import com.lightsecurity.core.Permission;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Utility method for manipulating <tt>GrantedAuthority</tt> collections etc.
 * <p>
 * Mainly intended for internal use.
 *
 * @author Luke Taylor
 */
public abstract class AuthorityUtils {
    public static final List<GrantedAuthority> NO_AUTHORITIES = Collections.emptyList();

    /**
     * Creates a array of GrantedAuthority objects from a comma-separated string
     * representation (e.g. "ROLE_A, ROLE_B, ROLE_C").
     *
     * @param authorityString the comma-separated string
     * @return the authorities created by tokenizing the string
     */
    public static List<GrantedAuthority> commaSeparatedStringToAuthorityList(
            String authorityString) {
        return createAuthorityList(StringUtils
                .tokenizeToStringArray(authorityString, ","));
    }

    /**
     * Converts an array of GrantedAuthority objects to a Set.
     * @return a Set of the Strings obtained from each call to
     * GrantedAuthority.getAuthority()
     */
    public static Set<String> authorityListToSet(
            Collection<? extends GrantedAuthority> userAuthorities) {
        Set<String> set = new HashSet<String>(userAuthorities.size());//获取权限的拦截url

        for (GrantedAuthority authority : userAuthorities) {
            set.add(authority.getAuthority().getPermissionPattern());
        }

        return set;
    }

    public static List<GrantedAuthority> createAuthorityList(String... permissionPattern) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(permissionPattern.length);

        for (String pattern : permissionPattern) {
            authorities.add(new DefaultGrantedAuthority(new DefaultPermission(pattern)));
        }

        return authorities;
    }
}
