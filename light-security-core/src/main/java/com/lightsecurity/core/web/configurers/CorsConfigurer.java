package com.lightsecurity.core.web.configurers;

import com.lightsecurity.core.config.annotation.web.HttpSecurityBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

public class CorsConfigurer<H extends HttpSecurityBuilder<H>> extends AbstractHttpConfigurer<CorsConfigurer<H>, H>{

    private static final String HANDLER_MAPPING_INTROSPECTOR = "org.springframework.web.servlet.handler.HandlerMappingIntrospector";
    private static final String CORS_CONFIGURATION_SOURCE_BEAN_NAME = "corsConfigurationSource";
    private static final String CORS_FILTER_BEAN_NAME = "corsFilter";

    private CorsConfigurationSource configurationSource;

    public CorsConfigurer() {
    }

    public CorsConfigurer<H> configurationSource(CorsConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
        return this;
    }

    @Override
    public void configure(H http) throws Exception {
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        CorsFilter corsFilter = getCorsFilter(context);
        if (corsFilter == null) {
            throw new IllegalStateException(
                    "Please configure either a " + CORS_FILTER_BEAN_NAME + " bean or a "
                            + CORS_CONFIGURATION_SOURCE_BEAN_NAME + "bean.");
        }
        http.addFilter(corsFilter);
    }

    private CorsFilter getCorsFilter(ApplicationContext context) {
        if (this.configurationSource != null){
            return new CorsFilter(this.configurationSource);
        }
        boolean containsCorsFilter = context.containsBeanDefinition(CORS_FILTER_BEAN_NAME);
        if (containsCorsFilter){
            return context.getBean(CORS_FILTER_BEAN_NAME, CorsFilter.class);
        }
        boolean containsCorsSource = context
                .containsBean(CORS_CONFIGURATION_SOURCE_BEAN_NAME);
        if (containsCorsSource) {
            CorsConfigurationSource configurationSource = context.getBean(
                    CORS_CONFIGURATION_SOURCE_BEAN_NAME, CorsConfigurationSource.class);
            return new CorsFilter(configurationSource);
        }

        boolean mvcPresent = ClassUtils.isPresent(HANDLER_MAPPING_INTROSPECTOR,
                context.getClassLoader());
        if (mvcPresent) {
            return MvcCorsFilter.getMvcCorsFilter(context);
        }
        return null;
    }

    static class MvcCorsFilter {
        /**
         * This needs to be isolated into a separate class as Spring MVC is an optional
         * dependency and will potentially cause ClassLoading issues
         * @param context
         * @return
         */
        private static CorsFilter getMvcCorsFilter(ApplicationContext context) {
            //todo 修改过时方法
            HandlerMappingIntrospector mappingIntrospector = new HandlerMappingIntrospector(
                    context);
            return new CorsFilter(mappingIntrospector);
        }
    }
}
