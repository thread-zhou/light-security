package com.lightsecurity.core.context;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractContextHolderStrategy<K, V> implements ContextHolderStrategy<K, V> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private AbstractContext  context;

    protected AbstractContextHolderStrategy(AbstractContext context){
        logger.info("当前实例全类名为：{}", this.getClass().getName());
        this.context = context;
    }

    @Override
    public AbstractContext getContextWrapper() {
        return context;
    }

    @Override
    public void put(K k, V v) {
        boolean flag = false;
        if (k == null || v == null){
            flag = true;
        }
        if (k instanceof String){
            if (StringUtils.isBlank((String)k)){
                flag = true;
            }
        }
        if (flag){
            if (logger.isWarnEnabled()){
                logger.warn("传入的key: {}或value: {}不合法", k, v);
            }
            throw new IllegalArgumentException("传入参数不合法");
        }
        context.put(k, v);
    }
}
