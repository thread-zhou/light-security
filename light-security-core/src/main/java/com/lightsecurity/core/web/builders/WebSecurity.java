package com.lightsecurity.core.web.builders;

import com.lightsecurity.core.config.annotation.AbstractConfiguredSecurityBuilder;
import com.lightsecurity.core.config.annotation.ObjectPostProcessor;
import com.lightsecurity.core.config.annotation.SecurityBuilder;
import com.lightsecurity.core.filter.DefaultSecurityFilterChain;
import com.lightsecurity.core.filter.FilterChainProxy;
import com.lightsecurity.core.filter.FilterSecurityInterceptor;
import com.lightsecurity.core.filter.SecurityFilterChain;
import com.lightsecurity.core.util.matcher.RequestMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

public final class WebSecurity extends AbstractConfiguredSecurityBuilder<Filter, WebSecurity> implements SecurityBuilder<Filter>, ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<RequestMatcher> ignoredRequests = new ArrayList<>();

    private final List<SecurityBuilder<? extends SecurityFilterChain>> securityFilterChainBuilders =  new ArrayList<>();

    private FilterSecurityInterceptor filterSecurityInterceptor;

    private Runnable postBuildAction =  new Runnable() {
        @Override
        public void run() {

        }
    };

    public WebSecurity(ObjectPostProcessor<Object> objectPostProcessor){
        super(objectPostProcessor);
    }

    @Override
    protected Filter performBuild() throws Exception {
        Assert.state(!securityFilterChainBuilders.isEmpty(), "At least one SecurityBuilder<? extends SecurityFilterChain> needs to be specified. Typically this done by adding a @Configuration that extends WebSecurityConfigurerAdapter. More advanced users can invoke"
                + WebSecurity.class.getSimpleName()
                + ".addSecurityFilterChainBuilder directly");
        int chainSize = ignoredRequests.size() + securityFilterChainBuilders.size();
        List<SecurityFilterChain> securityFilterChains = new ArrayList<>(chainSize);
        for (RequestMatcher ignoredRequest : ignoredRequests){
            securityFilterChains.add(new DefaultSecurityFilterChain(ignoredRequest));
        }
        for (SecurityBuilder<? extends SecurityFilterChain> securityFilterChainBuilder : securityFilterChainBuilders){
            securityFilterChains.add(securityFilterChainBuilder.build());
        }

        //创建FilterChainProxy
        FilterChainProxy filterChainProxy = new FilterChainProxy(securityFilterChains);
        filterChainProxy.afterPropertiesSet();//进行securityFilterChains的检查

        Filter result = filterChainProxy;

        postBuildAction.run();
        return result;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

    public WebSecurity addSecurityFilterChainBuilder(SecurityBuilder<? extends SecurityFilterChain> securityFilterChainBuilder){
        this.securityFilterChainBuilders.add(securityFilterChainBuilder);
        return this;
    }

    public WebSecurity postBuildAction(Runnable postBuildAction) {
        this.postBuildAction = postBuildAction;
        return this;
    }

    public WebSecurity securityInterceptor(FilterSecurityInterceptor securityInterceptor) {
        this.filterSecurityInterceptor = securityInterceptor;
        return this;
    }

}
