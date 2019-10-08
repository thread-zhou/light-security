package com.lightsecurity.core.filter;

import com.lightsecurity.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FilterChainProxy extends GenericFilter{

    private static final Logger logger = LoggerFactory.getLogger(FilterChainProxy.class);

    private List<SecurityFilterChain> filterChains;

    private FilterChainValidator filterChainValidator = new NullFilterChainValidator();

    public FilterChainProxy(){}

    public FilterChainProxy(SecurityFilterChain chain){
        this(Arrays.asList(chain));
    }

    public FilterChainProxy(List<SecurityFilterChain> filterChains){
        this.filterChains = filterChains;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        filterChainValidator.validate(this);//todo validate没有实现
    }

    /**
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            doFilterInternal(request, response, chain);
        }finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {


    }

    private List<Filter> getFilters(HttpServletRequest request){
        for (SecurityFilterChain chain : filterChains){
            if (chain.matches(request)){
                return chain.getFilters();
            }
        }
        return null;
    }



}
