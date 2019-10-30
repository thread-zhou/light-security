package com.lightsecurity.core.web.configurers;

import com.lightsecurity.core.config.annotation.web.HttpSecurityBuilder;
import com.lightsecurity.core.filter.LogoutFilter;
import com.lightsecurity.core.util.matcher.AntPathRequestMatcher;
import com.lightsecurity.core.util.matcher.OrRequestMatcher;
import com.lightsecurity.core.util.matcher.RequestMatcher;
import com.lightsecurity.core.web.logout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class LogoutConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractHttpConfigurer<LogoutConfigurer<H>, H> {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private List<LogoutHandler> logoutHandlers = new ArrayList<LogoutHandler>();
    private SecurityContextLogoutHandler contextLogoutHandler = new SecurityContextLogoutHandler();
    private String logoutSuccessUrl = "/login?logout";
    private LogoutSuccessHandler logoutSuccessHandler;
    private String logoutUrl = "/logout";
    private RequestMatcher logoutRequestMatcher;
    private boolean customLogoutSuccess;

    private LinkedHashMap<RequestMatcher, LogoutSuccessHandler> defaultLogoutSuccessHandlerMappings =
            new LinkedHashMap<RequestMatcher, LogoutSuccessHandler>();

    public LogoutConfigurer() {
    }


    public LogoutConfigurer<H> addLogoutHandler(LogoutHandler logoutHandler) {
        Assert.notNull(logoutHandler, "logoutHandler cannot be null");
        this.logoutHandlers.add(logoutHandler);
        return this;
    }

    public LogoutConfigurer<H> clearAuthentication(boolean clearAuthentication) {
        contextLogoutHandler.setClearAuthentication(clearAuthentication);
        return this;
    }


    public LogoutConfigurer<H> invalidateHttpSession(boolean invalidateHttpSession) {
        contextLogoutHandler.setInvalidateHttpSession(invalidateHttpSession);
        return this;
    }


    public LogoutConfigurer<H> logoutUrl(String logoutUrl) {
        this.logoutRequestMatcher = null;
        this.logoutUrl = logoutUrl;
        return this;
    }


    public LogoutConfigurer<H> logoutRequestMatcher(RequestMatcher logoutRequestMatcher) {
        this.logoutRequestMatcher = logoutRequestMatcher;
        return this;
    }


    public LogoutConfigurer<H> logoutSuccessUrl(String logoutSuccessUrl) {
        this.customLogoutSuccess = true;
        this.logoutSuccessUrl = logoutSuccessUrl;
        return this;
    }


    public LogoutConfigurer<H> deleteCookies(String... cookieNamesToClear) {
        return addLogoutHandler(new CookieClearingLogoutHandler(cookieNamesToClear));
    }

    public LogoutConfigurer<H> logoutSuccessHandler(
            LogoutSuccessHandler logoutSuccessHandler) {
        this.logoutSuccessUrl = null;
        this.customLogoutSuccess = true;
        this.logoutSuccessHandler = logoutSuccessHandler;
        return this;
    }


    public LogoutConfigurer<H> defaultLogoutSuccessHandlerFor(
            LogoutSuccessHandler handler, RequestMatcher preferredMatcher) {
        Assert.notNull(handler, "handler cannot be null");
        Assert.notNull(preferredMatcher, "preferredMatcher cannot be null");
        this.defaultLogoutSuccessHandlerMappings.put(preferredMatcher, handler);
        return this;
    }



    private LogoutSuccessHandler getLogoutSuccessHandler() {
        LogoutSuccessHandler handler = this.logoutSuccessHandler;
        if (handler == null) {
            handler = createDefaultSuccessHandler();
        }
        return handler;
    }

    private LogoutSuccessHandler createDefaultSuccessHandler() {
        SimpleUrlLogoutSuccessHandler urlLogoutHandler = new SimpleUrlLogoutSuccessHandler();
        urlLogoutHandler.setDefaultTargetUrl(logoutSuccessUrl);
        if(defaultLogoutSuccessHandlerMappings.isEmpty()) {
            return urlLogoutHandler;
        }
        DelegatingLogoutSuccessHandler successHandler = new DelegatingLogoutSuccessHandler(defaultLogoutSuccessHandlerMappings);
        successHandler.setDefaultLogoutSuccessHandler(urlLogoutHandler);
        return successHandler;
    }

    @Override
    public void init(H http) throws Exception {
        logger.warn("没有进行init的设置");
    }

    @Override
    public void configure(H http) throws Exception {
        LogoutFilter logoutFilter = createLogoutFilter(http);
        http.addFilter(logoutFilter);
    }

    /**
     * Returns true if the logout success has been customized via
     * {@link #logoutSuccessUrl(String)} or
     * {@link #logoutSuccessHandler(LogoutSuccessHandler)}.
     *
     * @return true if logout success handling has been customized, else false
     */
    boolean isCustomLogoutSuccess() {
        return customLogoutSuccess;
    }

    /**
     * Gets the logoutSuccesUrl or null if a
     * {@link #logoutSuccessHandler(LogoutSuccessHandler)} was configured.
     *
     * @return the logoutSuccessUrl
     */
    private String getLogoutSuccessUrl() {
        return logoutSuccessUrl;
    }

    /**
     * Gets the {@link LogoutHandler} instances that will be used.
     * @return the {@link LogoutHandler} instances. Cannot be null.
     */
    List<LogoutHandler> getLogoutHandlers() {
        return logoutHandlers;
    }

    /**
     * Creates the {@link LogoutFilter} using the {@link LogoutHandler} instances, the
     * {@link #logoutSuccessHandler(LogoutSuccessHandler)} and the
     * {@link #logoutUrl(String)}.
     *
     * @param http the builder to use
     * @return the {@link LogoutFilter} to use.
     * @throws Exception
     */
    private LogoutFilter createLogoutFilter(H http) throws Exception {
        logoutHandlers.add(contextLogoutHandler);
        LogoutHandler[] handlers = logoutHandlers
                .toArray(new LogoutHandler[logoutHandlers.size()]);
        LogoutFilter result = new LogoutFilter(getLogoutSuccessHandler(), handlers);
        result.setLogoutRequestMatcher(getLogoutRequestMatcher(http));
        result = postProcess(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    private RequestMatcher getLogoutRequestMatcher(H http) {
        if (logoutRequestMatcher != null) {
            return logoutRequestMatcher;
        }
//        if (http.getConfigurer(CsrfConfigurer.class) != null) {
//            this.logoutRequestMatcher = new AntPathRequestMatcher(this.logoutUrl, "POST");
//        }
        else {
            this.logoutRequestMatcher = new OrRequestMatcher(
                    new AntPathRequestMatcher(this.logoutUrl, "GET"),
                    new AntPathRequestMatcher(this.logoutUrl, "POST"),
                    new AntPathRequestMatcher(this.logoutUrl, "PUT"),
                    new AntPathRequestMatcher(this.logoutUrl, "DELETE")
            );
        }
        return this.logoutRequestMatcher;
    }
}
