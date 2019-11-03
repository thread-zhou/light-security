package com.lightsecurity.core.web.access;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.util.matcher.IpAddressMatcher;
import com.lightsecurity.core.web.FilterInvocation;

import javax.servlet.http.HttpServletRequest;

public class WebSecurityExpressionRoot extends SecurityExpressionRoot {
    // private FilterInvocation filterInvocation;
    /** Allows direct access to the request object */
    public final HttpServletRequest request;

    public WebSecurityExpressionRoot(Authentication a, FilterInvocation fi) {
        super(a);
        // this.filterInvocation = fi;
        this.request = fi.getRequest();
    }

    /**
     * Takes a specific IP address or a range using the IP/Netmask (e.g. 192.168.1.0/24 or
     * 202.24.0.0/14).
     *
     * @param ipAddress the address or range of addresses from which the request must
     * come.
     * @return true if the IP address of the current request is in the required range.
     */
    public boolean hasIpAddress(String ipAddress) {
        return (new IpAddressMatcher(ipAddress).matches(request));
    }

}

