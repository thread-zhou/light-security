package com.lightsecurity.core.config.annotation.web.configuration;

import com.lightsecurity.core.authentication.AuthenticationProvider;
import com.lightsecurity.core.authority.AuthenticationManagerBuilder;
import com.lightsecurity.core.config.GlobalAuthenticationConfigurerAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;

@Order(InitializeAuthenticationProviderBeanManagerConfigurer.DEFAULT_ORDER)
class InitializeAuthenticationProviderBeanManagerConfigurer extends GlobalAuthenticationConfigurerAdapter {

    static final int DEFAULT_ORDER = InitializeUserDetailsBeanManagerConfigurer.DEFAULT_ORDER
            - 100;

    private final ApplicationContext context;

    public InitializeAuthenticationProviderBeanManagerConfigurer(ApplicationContext context){
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
            AuthenticationProvider authenticationProvider = getBeanOrNull(
                    AuthenticationProvider.class);
            if (authenticationProvider == null) {
                return;
            }


            auth.authenticationProvider(authenticationProvider);
        }

        /**
         * @return
         */
        private <T> T getBeanOrNull(Class<T> type) {
            String[] userDetailsBeanNames = InitializeAuthenticationProviderBeanManagerConfigurer.this.context
                    .getBeanNamesForType(type);
            if (userDetailsBeanNames.length != 1) {
                return null;
            }

            return InitializeAuthenticationProviderBeanManagerConfigurer.this.context
                    .getBean(userDetailsBeanNames[0], type);
        }
    }
}
