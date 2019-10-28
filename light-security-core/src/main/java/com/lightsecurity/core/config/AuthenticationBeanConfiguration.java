package com.lightsecurity.core.config;

import com.lightsecurity.core.userdetails.DefaultUserDetailsService;
import com.lightsecurity.core.userdetails.UserDetailsService;
import com.lightsecurity.core.util.BCryptPasswordEncoder;
import com.lightsecurity.core.util.PasswordEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 认证相关的拓展点配置
 * 配置在这里的Bean, 都可以通过声明同类型或同名的Bean来覆盖
 */
@Configuration
public class AuthenticationBeanConfiguration {

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService userDetailsService(){
        return new DefaultUserDetailsService();
    }

}
