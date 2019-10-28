package com.lightsecurity.core.web.configurers;

import com.lightsecurity.core.config.annotation.web.HttpSecurityBuilder;
import com.lightsecurity.core.filter.UsernamePasswordAuthenticationFilter;
import com.lightsecurity.core.util.matcher.AntPathRequestMatcher;
import com.lightsecurity.core.util.matcher.RequestMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormLoginConfigurer<H extends HttpSecurityBuilder<H>> extends AbstractAuthenticationFilterConfigurer<H, FormLoginConfigurer<H>, UsernamePasswordAuthenticationFilter>{

    private Logger logger = LoggerFactory.getLogger(getClass());

    public FormLoginConfigurer(){
        super(new UsernamePasswordAuthenticationFilter(), null);

    }

    @Override
    public void init(H http) throws Exception {
        super.init(http);
        initDefaultLoginFilter(http);
    }

    private void initDefaultLoginFilter(H http) {
        if (logger.isDebugEnabled()){
            logger.debug("目前给定一个空实现");
        }
    }

    @Override
    protected RequestMatcher createLoginProcessUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }

    public FormLoginConfigurer<H> usernameParameter(String usernameParameter){
        getAuthenticationFilter().setUsernameParameter(usernameParameter);
        return this;
    }


    public FormLoginConfigurer<H> passwordParameter(String passwordParameter){
        getAuthenticationFilter().setPasswordParameter(passwordParameter);
        return this;
    }


}
