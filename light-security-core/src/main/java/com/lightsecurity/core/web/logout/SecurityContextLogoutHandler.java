package com.lightsecurity.core.web.logout;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.context.SecurityContext;
import com.lightsecurity.core.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SecurityContextLogoutHandler  implements LogoutHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());

    private boolean invalidateHttpSession = true;
    private boolean clearAuthentication = true;

    // ~ Methods
    // ========================================================================================================

    /**
     * Requires the request to be passed in.
     *
     * @param request from which to obtain a HTTP session (cannot be null)
     * @param response not used (can be <code>null</code>)
     * @param authentication not used (can be <code>null</code>)
     */
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {
        Assert.notNull(request, "HttpServletRequest required");
        if (invalidateHttpSession) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                logger.debug("Invalidating session: " + session.getId());
                session.invalidate();
            }
        }

        if (clearAuthentication) {
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(null);
        }

        SecurityContextHolder.clearContext();
    }

    public boolean isInvalidateHttpSession() {
        return invalidateHttpSession;
    }

    /**
     * Causes the {@link HttpSession} to be invalidated when this {@link LogoutHandler} is
     * invoked. Defaults to true.
     *
     * @param invalidateHttpSession true if you wish the session to be invalidated
     * (default) or false if it should not be.
     */
    public void setInvalidateHttpSession(boolean invalidateHttpSession) {
        this.invalidateHttpSession = invalidateHttpSession;
    }

    /**
     * If true, removes the {@link Authentication} from the {@link SecurityContext} to
     * prevent issues with concurrent requests.
     *
     * @param clearAuthentication true if you wish to clear the {@link Authentication}
     * from the {@link SecurityContext} (default) or false if the {@link Authentication}
     * should not be removed.
     */
    public void setClearAuthentication(boolean clearAuthentication) {
        this.clearAuthentication = clearAuthentication;
    }
}
