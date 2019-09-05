package com.lightsecurity.core.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 封装了HTTPServletRequest和HTTPServletResponse
 * 参考Spring Security 中的 HttpRequestResponseHolder
 */
public final class HttpRequestResponseHolder {

    private HttpServletRequest request;
    private HttpServletResponse response;

    public HttpRequestResponseHolder(HttpServletRequest request, HttpServletResponse response){
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
