package com.lightsecurity.core.config.annotation.web;

import com.lightsecurity.core.config.annotation.ObjectPostProcessor;
import com.lightsecurity.core.web.builders.WebSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;

@Order(100)
public abstract class WebSecurityConfigurerAdapter implements WebSecurityConfigurer<WebSecurity> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext context;

    private boolean disableDefaults;

    private ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<Object>() {
        @Override
        public <O> O postProcess(O object) {
            throw new IllegalStateException(
                    ObjectPostProcessor.class.getName()
                            + " is a required bean. Ensure you have used @EnableWebSecurity and @Configuration");
        }
    };

    protected WebSecurityConfigurerAdapter() {
        this(false);
    }

    protected WebSecurityConfigurerAdapter(boolean disableDefaults) {
        this.disableDefaults = disableDefaults;
    }

    @Override
    public void init(WebSecurity builder) throws Exception {
        logger.info("hello configurer init");
    }

    @Override
    public void configure(WebSecurity builder) throws Exception {

    }
}
