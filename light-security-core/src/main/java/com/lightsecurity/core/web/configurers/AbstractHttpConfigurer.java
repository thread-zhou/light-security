package com.lightsecurity.core.web.configurers;

import com.lightsecurity.core.config.annotation.ObjectPostProcessor;
import com.lightsecurity.core.config.annotation.SecurityConfigurerAdapter;
import com.lightsecurity.core.config.annotation.web.HttpSecurityBuilder;
import com.lightsecurity.core.filter.DefaultSecurityFilterChain;

public abstract class AbstractHttpConfigurer<T extends AbstractHttpConfigurer<T, B>, B extends HttpSecurityBuilder<B>>
        extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, B> {

    /**
     * 通过删除{@link AbstractHttpConfigurer}禁用它。 这样做之后，可以应用新版本的配置
     * @return
     */
    public B disable(){
        getBuilder().removeConfigurer(getClass());
        return getBuilder();
    }

    public T withObjectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor){
        addObjectPostProcessor(objectPostProcessor);
        return (T) this;
    }
}
