package com.lightsecurity.core.config.annotation.web.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({ObjectPostProcessorConfiguration.class})
@Configuration
public class AuthenticationConfiguration {
    //todo 后面增加
}
