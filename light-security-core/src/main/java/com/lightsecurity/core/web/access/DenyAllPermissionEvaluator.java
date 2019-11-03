package com.lightsecurity.core.web.access;

import com.lightsecurity.core.Authentication;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

public class DenyAllPermissionEvaluator implements PermissionEvaluator {

    private final Log logger = LogFactory.getLog(getClass());

    /**
     * @return false always
     */
    public boolean hasPermission(Authentication authentication, Object target,
                                 Object permission) {
        logger.warn("Denying user " + authentication.getName() + " permission '"
                + permission + "' on object " + target);
        return false;
    }

    /**
     * @return false always
     */
    public boolean hasPermission(Authentication authentication, Serializable targetId,
                                 String targetType, Object permission) {
        logger.warn("Denying user " + authentication.getName() + " permission '"
                + permission + "' on object with Id '" + targetId);
        return false;
    }

}

