package com.lightsecurity.core.web;

public interface PortMapper {

    Integer lookupHttpPort(Integer httpsPort);

    /**
     * Locates the HTTPS port associated with the specified HTTP port.
     * <P>
     * Returns <code>null</code> if unknown.
     * </p>
     *
     * @param httpPort
     *
     * @return the HTTPS port or <code>null</code> if unknown
     */
    Integer lookupHttpsPort(Integer httpPort);

}
