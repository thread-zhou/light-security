package com.lightsecurity.configurer;

import com.lightsecurity.core.config.annotation.web.WebSecurityConfigurerAdapter;
import com.lightsecurity.core.web.builders.WebSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void configure(WebSecurity builder) throws Exception {
        super.configure(builder);
        logger.debug("自定义测config执行");
    }
}
