package com.lightsecurity.core.config.annotation.web;

import com.lightsecurity.core.config.annotation.SecurityConfigurer;
import com.lightsecurity.core.web.builders.WebSecurity;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutowiredWebSecurityConfigurersIgnoreParents {

    private final ConfigurableListableBeanFactory beanFactory;

    public AutowiredWebSecurityConfigurersIgnoreParents(ConfigurableListableBeanFactory beanFactory){
        Assert.notNull(beanFactory, "beanFactory cannot be null");
        this.beanFactory = beanFactory;
    }

    public List<SecurityConfigurer<Filter, WebSecurity>> getWebSecurityConfigurers(){
        List<SecurityConfigurer<Filter, WebSecurity>> webSecurityConfigurers = new ArrayList<>();
        Map<String, WebSecurityConfigurer> beanOfType = beanFactory.getBeansOfType(WebSecurityConfigurer.class);
        for (Map.Entry<String, WebSecurityConfigurer> entry : beanOfType.entrySet()){
            webSecurityConfigurers.add(entry.getValue());
        }
        return webSecurityConfigurers;
    }

}
