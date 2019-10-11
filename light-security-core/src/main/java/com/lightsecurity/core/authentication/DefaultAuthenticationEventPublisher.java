package com.lightsecurity.core.authentication;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.authentication.event.*;
import com.lightsecurity.core.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.Assert;

import javax.security.auth.login.AccountExpiredException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Properties;

public class DefaultAuthenticationEventPublisher implements AuthenticationEventPublisher, ApplicationEventPublisherAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 存储 异常类类名以及该异常类型的事件处理类的构造器组成的key-value数据对
     */
    private final HashMap<String, Constructor<? extends AbstractAuthenticationEvent>> exceptionMappings = new HashMap<>();

    public DefaultAuthenticationEventPublisher(){
        this(null);
    }

    public DefaultAuthenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher){
        this.applicationEventPublisher = applicationEventPublisher;

        addMapping(BadCredentialsException.class.getName(), AuthenticationFailureCredentialsExpiredEvent.class);
        addMapping(UsernameNotFoundException.class.getName(), AuthenticationFailureCredentialsExpiredEvent.class);
        addMapping(AccountExpiredException.class.getName(), AuthenticationFailureExpiredEvent.class);
        addMapping(ProviderNotFoundException.class.getName(), AuthenticationFailureProviderNotFoundEvent.class);
        addMapping(CredentialsExpiredException.class.getName(), AuthenticationFailureCredentialsExpiredEvent.class);
    }



    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {
        //如果事件发布对象不为null, 则进行事件的发布
        if (applicationEventPublisher != null){
            applicationEventPublisher.publishEvent(new AuthenticationSuccessEvent(authentication));
        }
    }

    @Override
    public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        /**
         * 从缓存中取得对应的异常应用程序事件的构造器
         */
        Constructor<? extends AbstractAuthenticationEvent> constructor = exceptionMappings.get(exception.getClass().getName());
        AbstractAuthenticationEvent event = null;

        //根据构造器创建对象
        if (constructor != null){
            try {
                event = constructor.newInstance(authentication, exception);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        //如果应用程序事件和事件发布对象不为null, 则进行事件发布
        if (event != null){
            if (applicationEventPublisher != null){
                applicationEventPublisher.publishEvent(event);
            }
        }else {
            if (logger.isDebugEnabled()){
                logger.debug("No event was found for the exception " + exception.getClass().getName());
            }
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 为事件映射设置其他异常。 这些会自动合并为<code> ProviderManager </ code>定义的事件映射的默认例外。
     * @param additionalExceptionMappings 其中键是异常类的标准字符串名称，值是要触发的事件类的标准字符串名称。
     */
    public void setAdditionalExceptionMappings(Properties additionalExceptionMappings){
        Assert.notNull(additionalExceptionMappings, "The exceptionMappings object must not be null");
        for (Object exceptionClass : additionalExceptionMappings.keySet()){
            String eventClass = (String)additionalExceptionMappings.get(exceptionClass);
            try {
                Class<?> clazz = getClass().getClassLoader().loadClass(eventClass);
                Assert.isAssignable(AbstractAuthenticationFailureEvent.class, clazz);
                addMapping((String) exceptionClass, (Class<? extends AbstractAuthenticationFailureEvent>) clazz);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to load authentication event class "
                        + eventClass);
            }
        }
    }

    private void addMapping(String exceptionClass, Class<? extends AbstractAuthenticationFailureEvent> eventClass){
        //todo 完成该方法
        try {
            //Java 反射, 获取对应的构造器对象
            Constructor<? extends AbstractAuthenticationEvent> constructor = eventClass.getConstructor(Authentication.class, AuthenticationException.class);
            exceptionMappings.put(exceptionClass, constructor);
        }catch (NoSuchMethodException e){
            throw new RuntimeException("Authentication event class "
                    + eventClass.getName() + " has no suitable constructor");
        }
    }
}
