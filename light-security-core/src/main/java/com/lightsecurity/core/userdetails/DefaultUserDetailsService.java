package com.lightsecurity.core.userdetails;

import com.lightsecurity.core.exception.UsernameNotFoundException;
import com.lightsecurity.core.util.BCryptPasswordEncoder;

/**
 * 默认的UserDetailsService实现
 *
 */
public class DefaultUserDetailsService extends AbstractUserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new User("root", new BCryptPasswordEncoder().encode("root_123"));
    }
}
