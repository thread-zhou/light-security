package com.lightsecurity.core.filter;

import com.lightsecurity.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
            //重点
            SecurityContextHolder.clearContext();
        }
    }

    private void doFilterInternal(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        //todo
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        List<Filter> filters = getFilters(request);
        if (filters == null || filters.size() == 0){
            if (logger.isDebugEnabled()){
                logger.debug("当前请求没有符合的过滤器链, 请进行过滤器链匹配机制的检查");
            }
            chain.doFilter(request, response);
            return;
        }

        VirtualFilterChain virtualFilterChain = new VirtualFilterChain(request, chain, filters);
        virtualFilterChain.doFilter(request, response);
    }

    private List<Filter> getFilters(HttpServletRequest request){
        for (SecurityFilterChain chain : filterChains){
            if (chain.matches(request)){
                return chain.getFilters();
            }
        }
        return null;
    }

    private static class VirtualFilterChain implements FilterChain {

        //原始链
        private final FilterChain originalChain;
        //附加过滤器链
        private final List<Filter> additionalFilters;
        private HttpServletRequest request;//目前没有啥作用, SpringSecurity中存放的是经过封装的request, 有更多的接口可以使用
        private final int size;
        private int currentPosition = 0;

        private VirtualFilterChain(HttpServletRequest request, FilterChain chain, List<Filter> additionalFilters) {
            this.originalChain = chain;
            this.additionalFilters = additionalFilters;
            this.size = additionalFilters.size();
            this.request = request;
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
            if (currentPosition == size){
                if (logger.isDebugEnabled()){
                    logger.debug("到达附加过滤器的末端, 将继续原始链");
                }
                originalChain.doFilter(servletRequest, servletResponse);
            }else {
                currentPosition++;
                Filter nextFilter = additionalFilters.get(currentPosition - 1);//因为currentPosition先自加了1
                if (logger.isDebugEnabled()){
                    logger.debug("当前请求正在附加过滤器的第 {} 个位置, 当前作用的过滤器是: {}", currentPosition, nextFilter.getClass().getSimpleName());
                }
                nextFilter.doFilter(servletRequest, servletResponse, this);
            }
        }
    }


}
