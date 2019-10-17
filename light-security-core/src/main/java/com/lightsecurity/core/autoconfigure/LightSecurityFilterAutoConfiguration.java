package com.lightsecurity.core.autoconfigure;

import com.lightsecurity.core.properties.SecurityProperties;
import com.lightsecurity.core.web.context.AbstractSecurityWebApplicationInitializer;
import com.sun.org.apache.xerces.internal.parsers.SecurityConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties
@ConditionalOnClass({AbstractSecurityWebApplicationInitializer.class})
@AutoConfigureAfter(SecurityConfiguration.class)
public class LightSecurityFilterAutoConfiguration {

    private static final String DEFAULT_FILTER_NAME = AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME;

    /**
     * DelegatingFilterProxyRegistrationBean的作用是在springboot环境下通过内嵌容器(Tomcat等)启动类来注册一个DelegatingFilterProxy
     * @param securityProperties
     * @return
     */
    @Bean
    @ConditionalOnBean(name = DEFAULT_FILTER_NAME)
    public DelegatingFilterProxyRegistrationBean lightSecurityFilterChainRegistration(SecurityProperties securityProperties){
        DelegatingFilterProxyRegistrationBean delegatingFilterProxyRegistrationBean = new DelegatingFilterProxyRegistrationBean(DEFAULT_FILTER_NAME);
        delegatingFilterProxyRegistrationBean.setOrder(securityProperties.getFilterOrder());
        delegatingFilterProxyRegistrationBean.setDispatcherTypes(getDispatcherTypes(securityProperties));
        return delegatingFilterProxyRegistrationBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityProperties securityProperties(){
        return new SecurityProperties();
    }

    private EnumSet<DispatcherType> getDispatcherTypes(SecurityProperties securityProperties){
        if (securityProperties.getFilterDispatcherTypes() == null){
            return null;
        }
        Set<DispatcherType> dispatcherTypes = new HashSet<>();
        for (String dispatcherType : securityProperties.getFilterDispatcherTypes()){
            dispatcherTypes.add(DispatcherType.valueOf(dispatcherType));
        }
        return EnumSet.copyOf(dispatcherTypes);
    }

}
