package com.lightsecurity.core.filter;

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

    protected int weight;//当前过滤器的权重

    protected String[] processUrl;//当前过滤器作用的url

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String[] getProcessUrl() {
        return processUrl;
    }

    public void setProcessUrl(String[] processUrl) {
        this.processUrl = processUrl;
    }

    /**
     * 可以自定义实现一些装配工作
     */
    protected void genericInit(){

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
