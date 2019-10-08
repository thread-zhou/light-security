package com.lightsecurity.core.config.annotation.web.configuration;

import com.lightsecurity.core.config.annotation.ObjectPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 负责注册AutowireBeanFactoryObjectPostProcessor
 */
@Configuration
public class ObjectPostProcessorConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 注册AutowireBeanFactoryObjectPostProcessor
     * @param beanFactory
     * @return
     */
    @Bean
    public ObjectPostProcessor<Object> objectPostProcessor(AutowireCapableBeanFactory beanFactory){
        if (logger.isDebugEnabled()){
            logger.debug("成功初始化 --> AutowireBeanFactoryObjectPostProcessor");
        }
        return new AutowireBeanFactoryObjectPostProcessor(beanFactory);
    }

}
