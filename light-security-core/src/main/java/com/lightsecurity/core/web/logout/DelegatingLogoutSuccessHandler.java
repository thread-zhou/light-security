package com.lightsecurity.core.web.logout;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DelegatingLogoutSuccessHandler implements LogoutSuccessHandler {

    private final LinkedHashMap<RequestMatcher, LogoutSuccessHandler> matcherToHandler;

    private LogoutSuccessHandler defaultLogoutSuccessHandler;

    public DelegatingLogoutSuccessHandler(
            LinkedHashMap<RequestMatcher, LogoutSuccessHandler> matcherToHandler) {
        Assert.notEmpty(matcherToHandler, "matcherToHandler cannot be null");
        this.matcherToHandler = matcherToHandler;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        for (Map.Entry<RequestMatcher, LogoutSuccessHandler> entry : this.matcherToHandler
                .entrySet()) {
            RequestMatcher matcher = entry.getKey();
            if (matcher.matches(request)) {
                LogoutSuccessHandler handler = entry.getValue();
                handler.onLogoutSuccess(request, response, authentication);
                return;
            }
        }
        if (this.defaultLogoutSuccessHandler != null) {
            this.defaultLogoutSuccessHandler.onLogoutSuccess(request, response,
                    authentication);
        }
    }

    /**
     * Sets the default {@link LogoutSuccessHandler} if no other handlers available
     *
     * @param defaultLogoutSuccessHandler the defaultLogoutSuccessHandler to set
     */
    public void setDefaultLogoutSuccessHandler(
            LogoutSuccessHandler defaultLogoutSuccessHandler) {
        this.defaultLogoutSuccessHandler = defaultLogoutSuccessHandler;
    }

}
