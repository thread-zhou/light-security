package com.lightsecurity.core.filter;

import com.lightsecurity.core.util.matcher.RequestMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 参考 Spring Security 中 DefaultSecurityFilterChain 实现
 *
 * 默认过滤器链实现
 */
public final class DefaultSecurityFilterChain implements SecurityFilterChain {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSecurityFilterChain.class);
    private final RequestMatcher requestMatcher;
    private final List<Filter> filters;

    public DefaultSecurityFilterChain(RequestMatcher requestMatcher, Filter... filters){
        this(requestMatcher, Arrays.asList(filters));
    }

    public DefaultSecurityFilterChain(RequestMatcher requestMatcher, List<Filter> filters){
        logger.info("创建过滤器链: {}, {}", requestMatcher, filters );
        this.requestMatcher = requestMatcher;
        this.filters = new ArrayList<Filter>(filters);//为什么这里还要新建一个ArrayList
    }

    public RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }

    /**
     * 用于判断当前请求是否可以使用当前过滤器链
     * @param request
     * @return
     */
    @Override
    public boolean matches(HttpServletRequest request) {
        return requestMatcher.matches(request);
    }

    @Override
    public List<Filter> getFilters() {
        return filters;
    }

    @Override
    public String toString() {
        return "[ " + requestMatcher + ", " + filters + "]";
    }
}
