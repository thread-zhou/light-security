package com.lightsecurity.core.userdetails;

import com.lightsecurity.core.exception.UserDetailsServiceNotFoundException;
import com.lightsecurity.core.exception.UsernameNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认的UserDetailsService实现
 * 不做任何处理, 只在控制台打印一句日志, 然后抛出异常, 提醒系统自己配置UserDetailsService
 */
public class DefaultUserDetailsService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (logger.isWarnEnabled()){
            logger.warn("请配置 UserDetailsService 接口的实现.");
        }
        throw new UserDetailsServiceNotFoundException("请配置UserDetailsService接口的实现");
    }
}
