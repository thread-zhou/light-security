package com.lightsecurity.core.web;

import javax.servlet.ServletRequest;

public interface PortResolver {

    int getServerPort(ServletRequest request);

}
