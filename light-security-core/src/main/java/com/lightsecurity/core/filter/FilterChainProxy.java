package com.lightsecurity.core.filter;

import com.lightsecurity.core.context.SecurityContextHolder;
import com.lightsecurity.core.util.UrlUtils;
import com.lightsecurity.core.web.firewall.DefaultHttpFirewall;
import com.lightsecurity.core.web.firewall.FirewalledRequest;
import com.lightsecurity.core.web.firewall.HttpFirewall;
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

    private HttpFirewall firewall = new DefaultHttpFirewall();

    public FilterChainProxy(){}

    public FilterChainProxy(SecurityFilterChain chain){
        this(Arrays.asList(chain));
    }

    public FilterChainProxy(List<SecurityFilterChain> filterChains){
        this.filterChains = filterChains;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
//        super.afterPropertiesSet();
        filterChainValidator.validate(this);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        try {
//            doFilterInternal(request, response, chain);
//        }finally {
//            SecurityContextHolder.clearContext();
//        }
        doFilterInternal(request, response, chain);
    }

    private void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        FirewalledRequest fwRequest = firewall
                .getFirewalledRequest((HttpServletRequest) request);
        HttpServletResponse fwResponse = firewall
                .getFirewalledResponse((HttpServletResponse) response);

        List<Filter> filters = getFilters(fwRequest);

        if (filters == null || filters.size() == 0) {
            if (logger.isDebugEnabled()) {
                logger.debug(UrlUtils.buildRequestUrl(fwRequest)
                        + (filters == null ? " has no matching filters"
                        : " has an empty filter list"));
            }

            fwRequest.reset();

            chain.doFilter(fwRequest, fwResponse);

            return;
        }

        //内部类
        VirtualFilterChain vfc = new VirtualFilterChain(fwRequest, chain, filters);
        vfc.doFilter(fwRequest, fwResponse);
    }



    /**
     * Returns the first filter chain matching the supplied URL.
     *
     * @param request the request to match
     * @return an ordered array of Filters defining the filter chain
     */
    private List<Filter> getFilters(HttpServletRequest request) {
        for (SecurityFilterChain chain : filterChains) {
            if (chain.matches(request)) {
                return chain.getFilters();
            }
        }

        return null;
    }

    // ~ Inner Classes
    // ==================================================================================================

    /**
     * Internal {@code FilterChain} implementation that is used to pass a request through
     * the additional internal list of filters which match the request.
     */
    private static class VirtualFilterChain implements FilterChain {
        private final FilterChain originalChain;
        private final List<Filter> additionalFilters;
        private final FirewalledRequest firewalledRequest;
        private final int size;
        private int currentPosition = 0;

        private VirtualFilterChain(FirewalledRequest firewalledRequest,
                                   FilterChain chain, List<Filter> additionalFilters) {
            this.originalChain = chain;
            this.additionalFilters = additionalFilters;
            this.size = additionalFilters.size();
            this.firewalledRequest = firewalledRequest;
        }

        public void doFilter(ServletRequest request, ServletResponse response)
                throws IOException, ServletException {
            if (currentPosition == size) {
                if (logger.isDebugEnabled()) {
                    logger.debug(UrlUtils.buildRequestUrl(firewalledRequest)
                            + " reached end of additional filter chain; proceeding with original chain");
                }

                // Deactivate path stripping as we exit the security filter chain
                this.firewalledRequest.reset();

                originalChain.doFilter(request, response);
            }
            else {
                currentPosition++;

                Filter nextFilter = additionalFilters.get(currentPosition - 1);

                if (logger.isDebugEnabled()) {
                    logger.debug(UrlUtils.buildRequestUrl(firewalledRequest)
                            + " at position " + currentPosition + " of " + size
                            + " in additional filter chain; firing Filter: '"
                            + nextFilter.getClass().getSimpleName() + "'");
                }

                nextFilter.doFilter(request, response, this);
            }
        }
    }

    public interface FilterChainValidator {
        void validate(FilterChainProxy filterChainProxy);
    }

    private static class NullFilterChainValidator implements FilterChainValidator {
        public void validate(FilterChainProxy filterChainProxy) {
        }
    }
}
