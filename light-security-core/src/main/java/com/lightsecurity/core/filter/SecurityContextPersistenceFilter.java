package com.lightsecurity.core.filter;

import com.lightsecurity.core.context.HttpRequestResponseHolder;
import com.lightsecurity.core.context.SecurityContext;
import com.lightsecurity.core.context.SecurityContextHolder;
import com.lightsecurity.core.context.SecurityContextRepository;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 同 spring security 中的SecurityContextPersistenceFilter,
 * 参考其实现
 */
public class SecurityContextPersistenceFilter extends GenericFilter {

    private final SecurityContextRepository repository;
    private boolean forceEagerSessionCreation = false;

    public SecurityContextPersistenceFilter(SecurityContextRepository repository){
        this.repository = repository;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        final boolean debug = logger.isDebugEnabled();
        //并不是很明白这个属性的意义
        if (forceEagerSessionCreation){
            HttpSession session = request.getSession();
            if (debug && session.isNew()){
                logger.debug("Eagerly created session: " + session.getId());
            }
        }

        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext contextBeforeChainExecution =  repository.loadContext(holder);
        try {
            SecurityContextHolder.setContext(contextBeforeChainExecution);
            chain.doFilter(holder.getRequest(), holder.getResponse());
        }finally {
            SecurityContext contextAfterChainExecution = SecurityContextHolder.getContext();

            //Crucial removal of SecurityContextHolder contents - do this before anything
            //至关重要的操作,将ThreadLocal中的数据清除, 听说可以避免一些问题
            SecurityContextHolder.clearContext();

            repository.saveContext(contextAfterChainExecution, holder.getRequest(), holder.getResponse());
            if (debug){
                logger.debug("SecurityContextHolder 持有的ThreadLocal容器已经被清除, 当前请求处理完成.");
            }
        }
    }



    public void setForceEagerSessionCreation(boolean forceEagerSessionCreation) {
        this.forceEagerSessionCreation = forceEagerSessionCreation;
    }

    @Override
    protected void genericInit() {
        super.genericInit();
        //可以实现权重赋值
        this.weight = 0;
    }
}
