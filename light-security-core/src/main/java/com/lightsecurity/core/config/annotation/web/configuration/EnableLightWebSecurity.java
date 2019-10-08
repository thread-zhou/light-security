package com.lightsecurity.core.config.annotation.web.configuration;

import com.lightsecurity.core.config.WebSecurityConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * lightSecurity使用注解
 */
@Retention(value = RetentionPolicy.RUNTIME)//注解的生命周期
@Target(value = {ElementType.TYPE})//性质和Retention一样，都是注解类的属性，表示注解类应该在什么位置，对那一块的数据有效
@Documented
@Import({WebSecurityConfiguration.class})
@EnableGlobalAuthentication
@Configuration
public @interface EnableLightWebSecurity {
}
