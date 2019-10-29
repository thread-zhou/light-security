package com.lightsecurity.core.filter;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.authentication.AuthenticationTrustResolver;
import com.lightsecurity.core.authentication.AuthenticationTrustResolverImpl;
import com.lightsecurity.core.context.SecurityContextHolder;
import com.lightsecurity.core.exception.AccessDeniedException;
import com.lightsecurity.core.exception.AuthenticationException;
import com.lightsecurity.core.exception.InsufficientAuthenticationException;
import com.lightsecurity.core.util.ThrowableAnalyzer;
import com.lightsecurity.core.util.ThrowableCauseExtractor;
import com.lightsecurity.core.web.AuthenticationEntryPoint;
import com.lightsecurity.core.web.access.AccessDeniedHandler;
import com.lightsecurity.core.web.access.AccessDeniedHandlerImpl;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExceptionTranslationFilter extends GenericFilter {

    private AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandlerImpl();
    private AuthenticationEntryPoint authenticationEntryPoint;
    private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();
    private ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

    public ExceptionTranslationFilter(AuthenticationEntryPoint authenticationEntryPoint) {
        Assert.notNull(authenticationEntryPoint, "authenticationEntryPoint cannot be null");
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            filterChain.doFilter(request, response);

            logger.debug("Chain processed normally --> 正常行驶中...");
        }
        catch (IOException ex) {
            throw ex;
        }catch (Exception ex) {
            // Try to extract a SpringSecurityException from the stacktrace
            Throwable[] causeChain = throwableAnalyzer.determineCauseChain(ex);
            RuntimeException ase = (AuthenticationException) throwableAnalyzer
                    .getFirstThrowableOfType(AuthenticationException.class, causeChain);

            if (ase == null) {
                ase = (AccessDeniedException) throwableAnalyzer.getFirstThrowableOfType(
                        AccessDeniedException.class, causeChain);
            }

            if (ase != null) {
                handleLightSecurityException(request, response, filterChain, ase);
            }
            else {
                // Rethrow ServletExceptions and RuntimeExceptions as-is
                if (ex instanceof ServletException) {
                    throw (ServletException) ex;
                }
                else if (ex instanceof RuntimeException) {
                    throw (RuntimeException) ex;
                }

                // Wrap other Exceptions. This shouldn't actually happen
                // as we've already covered all the possibilities for doFilter
                throw new RuntimeException(ex);
            }
        }
    }

    public AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return authenticationEntryPoint;
    }

    protected AuthenticationTrustResolver getAuthenticationTrustResolver() {
        return authenticationTrustResolver;
    }

    private void handleLightSecurityException(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, RuntimeException exception) throws IOException, ServletException {
        if (exception instanceof AuthenticationException) {
            logger.debug(
                    "Authentication exception occurred; redirecting to authentication entry point",
                    exception);

            sendStartAuthentication(request, response, filterChain, (AuthenticationException) exception);
        }
        else if (exception instanceof AccessDeniedException) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authenticationTrustResolver.isAnonymous(authentication) || authenticationTrustResolver.isRememberMe(authentication)) {
                logger.debug(
                        "Access is denied (user is " + (authenticationTrustResolver.isAnonymous(authentication) ? "anonymous" : "not fully authenticated") + "); redirecting to authentication entry point",
                        exception);

                sendStartAuthentication(request, response, filterChain, new InsufficientAuthenticationException("Full authentication is required to access this resource"));
            }
            else {
                logger.debug(
                        "Access is denied (user is not anonymous); delegating to AccessDeniedHandler",
                        exception);

                accessDeniedHandler.handle(request, response,
                        (AccessDeniedException) exception);
            }
        }
    }

    protected void sendStartAuthentication(HttpServletRequest request,
                                           HttpServletResponse response, FilterChain chain,
                                           AuthenticationException reason) throws ServletException, IOException {
        // SEC-112: Clear the SecurityContextHolder's Authentication, as the
        // existing Authentication is no longer considered valid
        SecurityContextHolder.getContext().setAuthentication(null);
        logger.debug("Calling Authentication entry point.");
        authenticationEntryPoint.commence(request, response, reason);
    }

    public void setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
        Assert.notNull(accessDeniedHandler, "AccessDeniedHandler required");
        this.accessDeniedHandler = accessDeniedHandler;
    }

    public void setAuthenticationTrustResolver(
            AuthenticationTrustResolver authenticationTrustResolver) {
        Assert.notNull(authenticationTrustResolver,
                "authenticationTrustResolver must not be null");
        this.authenticationTrustResolver = authenticationTrustResolver;
    }

    public void setThrowableAnalyzer(ThrowableAnalyzer throwableAnalyzer) {
        Assert.notNull(throwableAnalyzer, "throwableAnalyzer must not be null");
        this.throwableAnalyzer = throwableAnalyzer;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        //必须被指定
        Assert.notNull(authenticationEntryPoint,"authenticationEntryPoint must be specified");
    }

    /**
     * Default implementation of <code>ThrowableAnalyzer</code> which is capable of also
     * unwrapping <code>ServletException</code>s.
     */
    private static final class DefaultThrowableAnalyzer extends ThrowableAnalyzer {
        /**
         * @see ThrowableAnalyzer#initExtractorMap()
         */
        protected void initExtractorMap() {
            super.initExtractorMap();

            registerExtractor(ServletException.class, new ThrowableCauseExtractor() {
                public Throwable extractCause(Throwable throwable) {
                    ThrowableAnalyzer.verifyThrowableHierarchy(throwable,
                            ServletException.class);
                    return ((ServletException) throwable).getRootCause();
                }
            });
        }

    }
}
