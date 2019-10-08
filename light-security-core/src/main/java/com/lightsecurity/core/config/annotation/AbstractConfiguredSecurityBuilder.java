package com.lightsecurity.core.config.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class AbstractConfiguredSecurityBuilder<O, B extends SecurityBuilder<O>> extends AbstractSecurityBuilder<O> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    //存储配置器,  通过apply和add方法 放置配置器
    private final LinkedHashMap<Class<? extends SecurityConfigurer<O, B>>, List<SecurityConfigurer<O, B>>> configurers = new LinkedHashMap<>();

    private final List<SecurityConfigurer<O, B>> configurersAddedInInitializing = new ArrayList<>();

    private BuildState buildState = BuildState.UN_BUILT;

    private final boolean allowConfigurersOfSameType;

    private ObjectPostProcessor<Object> objectPostProcessor;

    public <C extends SecurityConfigurer<O, B>> C apply(C configurer) throws Exception{
        add(configurer);
        return configurer;
    }
    private <C extends SecurityConfigurer<O, B>> void add(C configurer) throws Exception{
        Assert.notNull(configurer, "configurer cannot be null");

        Class<? extends SecurityConfigurer<O, B>> clazz = (Class<? extends SecurityConfigurer<O, B>>) configurer.getClass();
        synchronized (configurers) {
            if (buildState.isConfigured()) {
                throw new IllegalStateException("Cannot apply " + configurer
                        + " to already built object");
            }
            List<SecurityConfigurer<O, B>> configs = allowConfigurersOfSameType ? this.configurers.get(clazz) : null;//通常取值为null
            if (configs == null) {
                configs = new ArrayList<SecurityConfigurer<O, B>>(1);
            }
            configs.add(configurer);
            this.configurers.put(clazz, configs);
            if (buildState.isInitializing()) {
                this.configurersAddedInInitializing.add(configurer);
            }
        }
    }

    protected AbstractConfiguredSecurityBuilder(ObjectPostProcessor<Object> objectPostProcessor){
        this(objectPostProcessor, false);
    }

    protected AbstractConfiguredSecurityBuilder(ObjectPostProcessor<Object> objectPostProcessor, boolean allowConfigurersOfSameType){
        Assert.notNull(objectPostProcessor, "objectPostProcessor cannot be null");
        this.objectPostProcessor = objectPostProcessor;
        this.allowConfigurersOfSameType = allowConfigurersOfSameType;
    }

    @Override
    protected O doBuild() throws Exception {
        synchronized (configurers){
            buildState = BuildState.INITIALIZING;
            beforeInit();
            init();

            buildState = BuildState.CONFIGURING;
            beforeConfigure();
            configure();

            buildState = BuildState.BUILDING;

            O result = performBuild();

            buildState = BuildState.BUILT;
            return result;
        }
    }

    /**
     * 在调用init之前会先执行, 目前给定的是一个空实现, 子类可以重写此方法以挂接到生命周期
     * @throws Exception
     */
    protected void beforeInit() throws Exception{

    }

    private void init() throws Exception{
        Collection<SecurityConfigurer<O, B>> configurers =  getConfigurers();
        for (SecurityConfigurer<O, B> configurer : configurers){
            configurer.init((B)this);
        }

        for (SecurityConfigurer<O, B> configurer : configurersAddedInInitializing) {
            configurer.init((B) this);
        }
    }

    private Collection<SecurityConfigurer<O, B>> getConfigurers(){
        List<SecurityConfigurer<O, B>> result = new ArrayList<SecurityConfigurer<O, B>>();
        for (List<SecurityConfigurer<O, B>> configs : this.configurers.values()){
            result.addAll(configs);
        }
        return result;
    }

    /**
     * 在调用configure之前会先执行, 目前给定的是一个空实现, 子类可以重写此方法以挂接到生命周期
     * @throws Exception
     */
    protected void beforeConfigure() throws Exception{}

    private void configure() throws Exception{
        Collection<SecurityConfigurer<O, B>> configurers = getConfigurers();
        for (SecurityConfigurer<O, B> configurer : configurers){
            configurer.configure((B) this);
        }
    }

    /**
     * 子类必须实现此方法才能构建要返回的对象
     * @return
     * @throws Exception
     */
    protected abstract O performBuild() throws Exception;

    private static enum BuildState {
        /**
         * This is the state before the {@link Builder#build()} is invoked
         */
        UN_BUILT(0),

        /**
         * The state from when {@link Builder#build()} is first invoked until all the
         * {@link SecurityConfigurer#init(SecurityBuilder)} methods have been invoked.
         */
        INITIALIZING(1),

        /**
         * The state from after all {@link SecurityConfigurer#init(SecurityBuilder)} have
         * been invoked until after all the
         * {@link SecurityConfigurer#configure(SecurityBuilder)} methods have been
         * invoked.
         */
        CONFIGURING(2),

        /**
         * From the point after all the
         * {@link SecurityConfigurer#configure(SecurityBuilder)} have completed to just
         * after {@link AbstractConfiguredSecurityBuilder#performBuild()}.
         */
        BUILDING(3),

        /**
         * After the object has been completely built.
         */
        BUILT(4);

        private final int order;

        BuildState(int order) {
            this.order = order;
        }

        public boolean isInitializing() {
            return INITIALIZING.order == order;
        }

        /**
         * Determines if the state is CONFIGURING or later
         * @return
         */
        public boolean isConfigured() {
            return order >= CONFIGURING.order;
        }
    }
}
