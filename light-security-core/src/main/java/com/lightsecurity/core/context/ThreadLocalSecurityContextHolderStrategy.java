package com.lightsecurity.core.context;

import org.springframework.util.Assert;

public class ThreadLocalSecurityContextHolderStrategy implements SecurityContextHolderStrategy {

    private static final ThreadLocal<SecurityContext> CONTEXT_HOLDER = new ThreadLocal<SecurityContext>();

    public void clearContext() {
        CONTEXT_HOLDER.remove();
    }

    public SecurityContext getContext() {
        SecurityContext temp = CONTEXT_HOLDER.get();

        if (null == temp){
            temp = createEmptyContext();
            CONTEXT_HOLDER.set(temp);
        }
        return temp;
    }

    public void setContext(SecurityContext context) {
        Assert.notNull(context, "仅允许非空的SecurityContext实例");
        CONTEXT_HOLDER.set(context);
    }

    public SecurityContext createEmptyContext() {
        return new DefaultSecurityContextImpl();
    }
}
