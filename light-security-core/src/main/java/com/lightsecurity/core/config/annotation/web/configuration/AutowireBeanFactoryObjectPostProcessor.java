package com.lightsecurity.core.config.annotation.web.configuration;

import com.lightsecurity.core.config.annotation.ObjectPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

final class AutowireBeanFactoryObjectPostProcessor implements ObjectPostProcessor<Object>, DisposableBean, SmartInitializingSingleton {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final AutowireCapableBeanFactory autowireBeanFactory;
    private final List<SmartInitializingSingleton> smartSingletons = new ArrayList<SmartInitializingSingleton>();
    private final List<DisposableBean> disposableBeans = new ArrayList<DisposableBean>();

    public  AutowireBeanFactoryObjectPostProcessor(AutowireCapableBeanFactory autowireBeanFactory){
        Assert.notNull(autowireBeanFactory, "autowireBeanFactory cannot be null");
        this.autowireBeanFactory = autowireBeanFactory;
    }

    @Override
    public <O> O postProcess(O object) {
        if (object == null) {
            return null;
        }
        O result = null;
        try {
            result = (O) this.autowireBeanFactory.initializeBean(object,
                    object.toString());
        }
        catch (RuntimeException e) {
            Class<?> type = object.getClass();
            throw new RuntimeException(
                    "Could not postProcess " + object + " of type " + type, e);
        }
        this.autowireBeanFactory.autowireBean(object);
        if (result instanceof DisposableBean) {
            this.disposableBeans.add((DisposableBean) result);
        }
        if (result instanceof SmartInitializingSingleton) {
            this.smartSingletons.add((SmartInitializingSingleton) result);
        }
        return result;
    }

    @Override
    public void afterSingletonsInstantiated() {
        if (logger.isDebugEnabled()){
            logger.debug("SmartInitializingSingleton is ：{}", smartSingletons);
            logger.debug("SmartInitializingSingleton`s size ：{}", smartSingletons.size());
        }
        for(SmartInitializingSingleton singleton : smartSingletons) {
            singleton.afterSingletonsInstantiated();
        }
    }

    @Override
    public void destroy() throws Exception {
        for (DisposableBean disposable : this.disposableBeans) {
            try {
                disposable.destroy();
            }
            catch (Exception error) {
                this.logger.error(getClass().getName() + "destroy方法异常：{}", error);
            }
        }
    }
}
