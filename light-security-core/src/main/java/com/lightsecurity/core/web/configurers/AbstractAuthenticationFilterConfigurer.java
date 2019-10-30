package com.lightsecurity.core.web.configurers;

import com.lightsecurity.core.authentication.AuthenticationDetailsSource;
import com.lightsecurity.core.authentication.AuthenticationManager;
import com.lightsecurity.core.authentication.SimpleUrlAuthenticationFailureHandler;
import com.lightsecurity.core.authentication.handler.AuthenticationFailureHandler;
import com.lightsecurity.core.config.annotation.web.HttpSecurityBuilder;
import com.lightsecurity.core.filter.AbstractAuthenticationProcessingFilter;
import com.lightsecurity.core.util.matcher.*;
import com.lightsecurity.core.web.LoginUrlAuthenticationEntryPoint;
import com.lightsecurity.core.web.PortMapper;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

public abstract class AbstractAuthenticationFilterConfigurer<B extends HttpSecurityBuilder<B>, T extends AbstractAuthenticationFilterConfigurer<B, T, F>, F extends AbstractAuthenticationProcessingFilter>
        extends AbstractHttpConfigurer<T, B> {

    private final F authFilter;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    private String loginPage;

    private String failureUrl;

    private boolean customLoginPage;

    private AuthenticationFailureHandler failureHandler;

    private String loginProcessingUrl;

    private LoginUrlAuthenticationEntryPoint authenticationEntryPoint;

    protected AbstractAuthenticationFilterConfigurer(F authFilter, String defaultLoginProcessUrl){
        this.authFilter = authFilter;
        setLoginPage("/login");
        if (defaultLoginProcessUrl != null){
            loginProcessingUrl(defaultLoginProcessUrl);
        }
    }

    public String getLoginPage() {
        return loginPage;
    }

    private void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
        this.authenticationEntryPoint = new LoginUrlAuthenticationEntryPoint(loginPage);
    }

    protected final F getAuthenticationFilter(){
        return authFilter;
    }

    @Override
    public void configure(B http) throws Exception {
        PortMapper portMapper = http.getSharedObject(PortMapper.class);
        if (portMapper != null) {
            authenticationEntryPoint.setPortMapper(portMapper);
        }
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        authFilter.setAuthenticationManager(authenticationManager);
        //设置失败和成功处理器 todo
        if (authenticationDetailsSource != null){
            authFilter.setAuthenticationDetailsSource(authenticationDetailsSource);
        }
        F filter = postProcess(authFilter);
        http.addFilter(filter);
    }



    @Override
    public void init(B http) throws Exception {
        updateAuthenticationDefaults();
        registerDefaultAuthenticationEntryPoint(http);
    }

    @SuppressWarnings("unchecked")
    private void registerDefaultAuthenticationEntryPoint(B http) {
        ExceptionHandlingConfigurer<B> exceptionHandling = http.getConfigurer(ExceptionHandlingConfigurer.class);
        if (exceptionHandling == null) {
            return;
        }
        ContentNegotiationStrategy contentNegotiationStrategy = http.getSharedObject(ContentNegotiationStrategy.class);
        if (contentNegotiationStrategy == null) {
            contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
        }

        MediaTypeRequestMatcher mediaMatcher = new MediaTypeRequestMatcher(
                contentNegotiationStrategy, MediaType.APPLICATION_XHTML_XML,
                new MediaType("image", "*"), MediaType.TEXT_HTML, MediaType.TEXT_PLAIN);
        mediaMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));

        RequestMatcher notXRequestedWith = new NegatedRequestMatcher(
                new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));

        RequestMatcher preferredMatcher = new AndRequestMatcher(Arrays.asList(notXRequestedWith, mediaMatcher));

        exceptionHandling.defaultAuthenticationEntryPointFor(
                postProcess(authenticationEntryPoint), preferredMatcher);
    }

    public final T failureHandler(
            AuthenticationFailureHandler authenticationFailureHandler) {
        this.failureUrl = null;
        this.failureHandler = authenticationFailureHandler;
        return getSelf();
    }

    public final T failureUrl(String authenticationFailureUrl) {
        T result = failureHandler(new SimpleUrlAuthenticationFailureHandler(
                authenticationFailureUrl));
        this.failureUrl = authenticationFailureUrl;
        return result;
    }

    protected T loginPage(String loginPage) {
        setLoginPage(loginPage);
        updateAuthenticationDefaults();
        this.customLoginPage = true;
        return getSelf();
    }

    private void updateAuthenticationDefaults() {
        if (loginProcessingUrl == null) {
            loginProcessingUrl(loginPage);
        }
        if (failureHandler == null) {
            failureUrl(loginPage + "?error");
        }

        final LogoutConfigurer<B> logoutConfigurer = getBuilder().getConfigurer(
                LogoutConfigurer.class);
        if (logoutConfigurer != null && !logoutConfigurer.isCustomLogoutSuccess()) {
            logoutConfigurer.logoutSuccessUrl(loginPage + "?logout");
        }
    }

    protected T loginProcessingUrl(String loginProcessingUrl){
        this.loginProcessingUrl = loginProcessingUrl;
        authFilter.setRequestMatcher(createLoginProcessUrlMatcher(loginProcessingUrl));
        return getSelf();
    }

    private T getSelf() {
        return (T) this;
    }

    protected abstract RequestMatcher createLoginProcessUrlMatcher(String loginProcessingUrl);
}
