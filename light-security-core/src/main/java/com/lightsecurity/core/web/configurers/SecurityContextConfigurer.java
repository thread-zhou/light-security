package com.lightsecurity.core.web.configurers;

import com.lightsecurity.core.config.annotation.web.HttpSecurityBuilder;
import com.lightsecurity.core.config.annotation.web.configuration.AbstractHttpConfigurer;
import com.lightsecurity.core.context.HttpSessionSecurityContextRepository;
import com.lightsecurity.core.context.SecurityContextRepository;
import com.lightsecurity.core.filter.SecurityContextPersistenceFilter;

public final class SecurityContextConfigurer<H extends HttpSecurityBuilder<H>> extends AbstractHttpConfigurer<SecurityContextConfigurer<H>, H> {

    /**
     * Creates a new instance
     */
    public SecurityContextConfigurer(){}

    /**
     * 指定要共享的@{@link SecurityContextRepository}
     * @param securityContextRepository
     * @return 返回{@link HttpSecurity}进行进一步的自定义
     */
    public SecurityContextConfigurer<H> securityContextRepository(SecurityContextRepository securityContextRepository){
        getBuilder().setSharedObject(SecurityContextRepository.class, securityContextRepository);
        return this;
    }

    @Override
    public void configure(H http) throws Exception {
        SecurityContextRepository  securityContextRepository = http.getSharedObject(SecurityContextRepository.class);
        if (securityContextRepository == null){
            securityContextRepository = new HttpSessionSecurityContextRepository();
        }
        //初始化SecurityContextPersistenceFilter
        SecurityContextPersistenceFilter securityContextPersistenceFilter =  new SecurityContextPersistenceFilter(securityContextRepository);
        securityContextPersistenceFilter = postProcess(securityContextPersistenceFilter);//不明白为什么还要执行这个方法
        http.addFilter(securityContextPersistenceFilter);
    }
}
