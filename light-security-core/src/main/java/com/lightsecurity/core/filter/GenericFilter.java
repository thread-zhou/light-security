package com.lightsecurity.core.filter;

import com.lightsecurity.core.util.matcher.AntPathRequestMatcher;
import com.lightsecurity.core.util.matcher.RequestMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.Serializable;
import java.util.Enumeration;

/**
 * 通用的Filter, 之后所有的自定义Filter都将继承该类, 参考org.apache.catalina.servlet4preview.GenericFilter实现
 */
public abstract class GenericFilter implements Filter, FilterConfig, InitializingBean, Serializable {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected volatile FilterConfig filterConfig;

    private RequestMatcher requestMatcher;//当前过滤器作用的url封装

    public RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }

    protected void setRequestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    /**
     * 可以自定义实现一些装配工作
     */
    protected void genericInit(){

    }

    protected void setFilterProcessUrl(String filterProcessUrl){
        this.setRequestMatcher(new AntPathRequestMatcher(filterProcessUrl));
    }

    public String getInitParameter(String name) {
        return getFilterConfig().getInitParameter(name);
    }

    public Enumeration<String> getInitParameterNames() {
        return getFilterConfig().getInitParameterNames();
    }

    public ServletContext getServletContext() {
        return getFilterConfig().getServletContext();
    }

    public String getFilterName() {
        return getFilterConfig().getFilterName();
    }

    public FilterConfig getFilterConfig(){
        return filterConfig;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        if (logger.isDebugEnabled()){
            logger.debug("{} 初始化 ...", this.getClass().getName());
        }
    }

    @Override
    public void destroy() {
        if (logger.isDebugEnabled()){
            logger.debug("{} 销毁 ...", this.getClass().getName());
        }
    }

    /**
     * 在属性设置完毕后自动执行
     * @throws ServletException
     */
    public void afterPropertiesSet() throws ServletException {
        genericInit();
    }

}
