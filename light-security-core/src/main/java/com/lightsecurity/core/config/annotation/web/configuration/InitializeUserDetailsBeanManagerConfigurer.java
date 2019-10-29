package com.lightsecurity.core.config.annotation.web.configuration;

import com.lightsecurity.core.authentication.dao.DaoAuthenticationProvider;
import com.lightsecurity.core.authority.AuthenticationManagerBuilder;
import com.lightsecurity.core.config.GlobalAuthenticationConfigurerAdapter;
import com.lightsecurity.core.userdetails.UserDetailsService;
import com.lightsecurity.core.util.PasswordEncoder;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(InitializeUserDetailsBeanManagerConfigurer.DEFAULT_ORDER)
class InitializeUserDetailsBeanManagerConfigurer extends GlobalAuthenticationConfigurerAdapter {

    static final int DEFAULT_ORDER = Ordered.LOWEST_PRECEDENCE - 5000;

    private final ApplicationContext context;

    /**
     * @param context
     */
    public InitializeUserDetailsBeanManagerConfigurer(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void init(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.apply(new InitializeUserDetailsManagerConfigurer());
    }

    class InitializeUserDetailsManagerConfigurer
            extends GlobalAuthenticationConfigurerAdapter {
        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            if (auth.isConfigured()) {
                return;
            }
            UserDetailsService userDetailsService = getBeanOrNull(
                    UserDetailsService.class);
            if (userDetailsService == null) {
                return;
            }

            PasswordEncoder passwordEncoder = getBeanOrNull(PasswordEncoder.class);

            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setUserDetailsService(userDetailsService);
            if (passwordEncoder != null) {
                provider.setPasswordEncoder(passwordEncoder);
            }

            auth.authenticationProvider(provider);
        }

        /**
         * @return
         */
        private <T> T getBeanOrNull(Class<T> type) {
            String[] userDetailsBeanNames = InitializeUserDetailsBeanManagerConfigurer.this.context
                    .getBeanNamesForType(type);
            if (userDetailsBeanNames.length != 1) {
                return null;
            }

            return InitializeUserDetailsBeanManagerConfigurer.this.context
                    .getBean(userDetailsBeanNames[0], type);
        }
    }
}
