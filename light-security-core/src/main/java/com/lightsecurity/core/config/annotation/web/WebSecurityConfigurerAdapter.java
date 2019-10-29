package com.lightsecurity.core.config.annotation.web;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.authentication.AuthenticationManager;
import com.lightsecurity.core.authentication.AuthenticationTrustResolver;
import com.lightsecurity.core.authentication.AuthenticationTrustResolverImpl;
import com.lightsecurity.core.authentication.DefaultAuthenticationEventPublisher;
import com.lightsecurity.core.authority.AuthenticationManagerBuilder;
import com.lightsecurity.core.config.annotation.ObjectPostProcessor;
import com.lightsecurity.core.config.annotation.web.configuration.AbstractHttpConfigurer;
import com.lightsecurity.core.config.annotation.web.configuration.AuthenticationConfiguration;
import com.lightsecurity.core.exception.AuthenticationException;
import com.lightsecurity.core.exception.UsernameNotFoundException;
import com.lightsecurity.core.filter.FilterSecurityInterceptor;
import com.lightsecurity.core.userdetails.UserDetails;
import com.lightsecurity.core.userdetails.UserDetailsService;
import com.lightsecurity.core.web.builders.HttpSecurity;
import com.lightsecurity.core.web.builders.WebSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

import java.lang.reflect.Field;
import java.util.*;

@Order(100)
public abstract class WebSecurityConfigurerAdapter implements WebSecurityConfigurer<WebSecurity> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;

    /**
     * 是否启用默认配置: true表示禁用默认配置, false表示启用默认配置
     */
    private boolean disableDefaults;

    private AuthenticationConfiguration authenticationConfiguration;

    private AuthenticationManagerBuilder authenticationManagerBuilder;
    private AuthenticationManagerBuilder localConfigureAuthenticationBuilder;

    private AuthenticationManager authenticationManager;

    private ContentNegotiationStrategy contentNegotiationStrategy = new HeaderContentNegotiationStrategy();

    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();




    private HttpSecurity httpSecurity;

    private boolean authenticationManagerInitialized;

    private boolean disableLocalConfigureAuthenticationBuilder;


    /**
     * 使用缺省配置创建实例
     */
    protected WebSecurityConfigurerAdapter() {
        this(false);
    }

    /**
     * 创建一个实例, 这里提供指定是否应用默认配置
     * 禁用默认配置被认为是更高级的用法, 因为这需要对框架的实现方式有更多的了解
     * @param disableDefaults true表示禁用默认配置, false表示启用默认配置
     */
    protected WebSecurityConfigurerAdapter(boolean disableDefaults) {
        this.disableDefaults = disableDefaults;
    }


    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setAuthenticationConfiguration(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Autowired
    public void setObjectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor) {
        this.objectPostProcessor = objectPostProcessor;

        //创建AuthenticationManagerBuilder --> authenticationManagerBuilder
        authenticationManagerBuilder = new AuthenticationManagerBuilder(objectPostProcessor);

        //创建AuthenticationManagerBuilder --> localConfigureAuthenticationBuilder(匿名内部类)
        localConfigureAuthenticationBuilder = new AuthenticationManagerBuilder(objectPostProcessor){
            @Override
            public AuthenticationManagerBuilder eraseCredentials(boolean eraseCredentials) {
                return super.eraseCredentials(eraseCredentials);
            }
        };
    }

    protected final ApplicationContext getApplicationContext(){
        return this.applicationContext;
    }

    private ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<Object>() {
        @Override
        public <O> O postProcess(O object) {
            throw new IllegalStateException(
                    ObjectPostProcessor.class.getName()
                            + " is a required bean. Ensure you have used @EnableWebSecurity and @Configuration");
        }
    };


    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        this.disableLocalConfigureAuthenticationBuilder = true;
    }


    /**
     * 创建HttpSecurity对象
     * @return
     * @throws Exception
     */
    protected final HttpSecurity getHttpSecurity() throws Exception{
        if (httpSecurity != null){
            return httpSecurity;
        }
        //创建默认的时间发布类对象
        DefaultAuthenticationEventPublisher eventPublisher = objectPostProcessor.postProcess(new DefaultAuthenticationEventPublisher());

        //将事件发布实例注入localConfigureAuthenticationBuilder
        localConfigureAuthenticationBuilder.authenticationEventPublisher(eventPublisher);

        AuthenticationManager authenticationManager = authenticationManager();
        //将authenticationManager注入到authenticationMangerBuilder中
        authenticationManagerBuilder.parentAuthenticationManager(authenticationManager);

        Map<Class<? extends Object>, Object> sharedObjects = createSharedObjects();
        httpSecurity = new HttpSecurity(objectPostProcessor, authenticationManagerBuilder, sharedObjects);

        //disableDefaults  为true表示禁用默认配置, 反之表示使用默认配置
        if (!disableDefaults){
            //这里也是httpSecurity的默认配置, 但是存在开启条件--> disableDefaults

            httpSecurity.cors().and()
                    .securityContext().and()
                    .formLogin().and()
                    .anonymous();
//            http
//                    .csrf().and()
//                    .addFilter(new WebAsyncManagerIntegrationFilter())
//                    .exceptionHandling().and()
//                    .headers().and()
//                    .sessionManagement().and()
//                    .securityContext().and()
//                    .requestCache().and()
//                    .anonymous().and()
//                    .servletApi().and()
//                    .apply(new DefaultLoginPageConfigurer<HttpSecurity>()).and()
//                    .logout();
            ClassLoader classLoader = this.applicationContext.getClassLoader();
            List<AbstractHttpConfigurer> defaultHttpConfigurers = SpringFactoriesLoader.loadFactories(AbstractHttpConfigurer.class, classLoader);
            for(AbstractHttpConfigurer configurer : defaultHttpConfigurers) {
                httpSecurity.apply(configurer);
            }
        }
        configure(httpSecurity);
        return httpSecurity;
    }

    /**
     * 获取authenticationManager对象
     * @return
     * @throws Exception
     */
    protected AuthenticationManager authenticationManager() throws Exception{
        //todo 回顾本方法流程，并理解内容
        //判断是否已经进行了初始化
        if (!authenticationManagerInitialized){

            //将disableLocalConfigureAuthenticationBuilder属性强制为true
            configure(localConfigureAuthenticationBuilder);

            if (disableLocalConfigureAuthenticationBuilder){
                //获取authenticationManager实例
                authenticationManager = authenticationConfiguration.getAuthenticationManager();
            }else {
                //会执行到这里吗
                authenticationManager = localConfigureAuthenticationBuilder.build();
            }
            authenticationManagerInitialized = true;
        }
        return authenticationManager;
    }

    private Map<Class<? extends Object>, Object> createSharedObjects() {
        Map<Class<? extends Object>, Object> sharedObjects = new HashMap<Class<? extends Object>, Object>();
        sharedObjects.putAll(localConfigureAuthenticationBuilder.getSharedObjects());
        sharedObjects.put(UserDetailsService.class, userDetailsService());
        sharedObjects.put(ApplicationContext.class, applicationContext);
        sharedObjects.put(ContentNegotiationStrategy.class, contentNegotiationStrategy);
        sharedObjects.put(AuthenticationTrustResolver.class, trustResolver);
        return sharedObjects;
    }

    protected UserDetailsService userDetailsService() {
        AuthenticationManagerBuilder globalAuthBuilder = applicationContext.getBean(AuthenticationManagerBuilder.class);
        return new UserDetailsServiceDelegator(Arrays.asList(
                localConfigureAuthenticationBuilder, globalAuthBuilder));
    }

    @Override
    public void init(WebSecurity webSecurity) throws Exception {
        //todo 完成init方法
        logger.info("hello configurer init");
        final HttpSecurity httpSecurity = getHttpSecurity();
        webSecurity.addSecurityFilterChainBuilder(httpSecurity).postBuildAction(new Runnable() {
            @Override
            public void run() {
                FilterSecurityInterceptor securityInterceptor =  httpSecurity.getSharedObject(FilterSecurityInterceptor.class);
                webSecurity.securityInterceptor(securityInterceptor);
            }
        });
    }

    /**
     * 重写此方法以配置{@link WebSecurity}. 例如, 如果您希望忽略某些请求
     * 目前是一个空实现
     * @param builder
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity builder) throws Exception {
    }

    /**
     * 重写此方法已配置{@link HttpSecurity}.
     * 通常, 子类不应通过调用super来调用此方法, 因为可能会覆盖其配置
     * 其默认配置为:
     * <pre>
     * http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();
     * </pre>
     * @param httpSecurity
     * @throws Exception
     */
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        //todo 完成configure(HttpSecurity http)方法
        logger.debug("Using default configure(HttpSecurity). If subclassed this will potentially override subclass configure(HttpSecurity).");

//        httpSecurity.cors();
    }

    static final class UserDetailsServiceDelegator implements UserDetailsService {
        private List<AuthenticationManagerBuilder> delegateBuilders;
        private UserDetailsService delegate;
        private final Object delegateMonitor = new Object();

        UserDetailsServiceDelegator(List<AuthenticationManagerBuilder> delegateBuilders) {
            if (delegateBuilders.contains(null)) {
                throw new IllegalArgumentException(
                        "delegateBuilders cannot contain null values. Got "
                                + delegateBuilders);
            }
            this.delegateBuilders = delegateBuilders;
        }

        public UserDetails loadUserByUsername(String username)
                throws UsernameNotFoundException {
            if (delegate != null) {
                return delegate.loadUserByUsername(username);
            }

            synchronized (delegateMonitor) {
                if (delegate == null) {
                    for (AuthenticationManagerBuilder delegateBuilder : delegateBuilders) {
                        delegate = delegateBuilder.getDefaultUserDetailsService();
                        if (delegate != null) {
                            break;
                        }
                    }

                    if (delegate == null) {
                        throw new IllegalStateException("UserDetailsService is required.");
                    }
                    this.delegateBuilders = null;
                }
            }

            return delegate.loadUserByUsername(username);
        }
    }

    static final class AuthenticationManagerDelegator implements AuthenticationManager {
        private AuthenticationManagerBuilder delegateBuilder;
        private AuthenticationManager delegate;
        private final Object delegateMonitor = new Object();
        private Set<String> beanNames;

        AuthenticationManagerDelegator(AuthenticationManagerBuilder delegateBuilder,
                                       ApplicationContext context) {
            Assert.notNull(delegateBuilder, "delegateBuilder cannot be null");
            Field parentAuthMgrField = ReflectionUtils.findField(
                    AuthenticationManagerBuilder.class, "parentAuthenticationManager");
            ReflectionUtils.makeAccessible(parentAuthMgrField);
            beanNames = getAuthenticationManagerBeanNames(context);
            validateBeanCycle(
                    ReflectionUtils.getField(parentAuthMgrField, delegateBuilder),
                    beanNames);
            this.delegateBuilder = delegateBuilder;
        }

        public Authentication authenticate(Authentication authentication)
                throws AuthenticationException {
            if (delegate != null) {
                return delegate.authenticate(authentication);
            }

            synchronized (delegateMonitor) {
                if (delegate == null) {
                    delegate = this.delegateBuilder.getObject();
                    this.delegateBuilder = null;
                }
            }

            return delegate.authenticate(authentication);
        }

        private static Set<String> getAuthenticationManagerBeanNames(
                ApplicationContext applicationContext) {
            String[] beanNamesForType = BeanFactoryUtils
                    .beanNamesForTypeIncludingAncestors(applicationContext,
                            AuthenticationManager.class);
            return new HashSet<String>(Arrays.asList(beanNamesForType));
        }

        private static void validateBeanCycle(Object auth, Set<String> beanNames) {
            if (auth != null && !beanNames.isEmpty()) {
                if (auth instanceof Advised) {
                    Advised advised = (Advised) auth;
                    TargetSource targetSource = advised.getTargetSource();
                    if (targetSource instanceof LazyInitTargetSource) {
                        LazyInitTargetSource lits = (LazyInitTargetSource) targetSource;
                        if (beanNames.contains(lits.getTargetBeanName())) {
                            throw new FatalBeanException(
                                    "A dependency cycle was detected when trying to resolve the AuthenticationManager. Please ensure you have configured authentication.");
                        }
                    }
                }
                beanNames = Collections.emptySet();
            }
        }
    }
}
