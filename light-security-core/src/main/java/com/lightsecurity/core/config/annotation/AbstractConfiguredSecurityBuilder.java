package com.lightsecurity.core.config.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.*;

public abstract class AbstractConfiguredSecurityBuilder<O, B extends SecurityBuilder<O>> extends AbstractSecurityBuilder<O> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    //存储配置器,  通过apply和add方法 放置配置器
    private final LinkedHashMap<Class<? extends SecurityConfigurer<O, B>>, List<SecurityConfigurer<O, B>>> configurers = new LinkedHashMap<>();

    private final List<SecurityConfigurer<O, B>> configurersAddedInInitializing = new ArrayList<>();

    private BuildState buildState = BuildState.UN_BUILT;

    private final boolean allowConfigurersOfSameType;

    private ObjectPostProcessor<Object> objectPostProcessor;

    private final Map<Class<? extends Object>, Object> sharedObjects = new HashMap<>();

    public Map<Class<? extends Object>, Object> getSharedObjects() {
        return Collections.unmodifiableMap(this.sharedObjects);
    }

    public <C> void setSharedObject(Class<C> sharedType, C object) {
        this.sharedObjects.put(sharedType, object);
    }

    public <C> C getSharedObject(Class<C> sharedType) {
        return (C) this.sharedObjects.get(sharedType);
    }

    public <C extends  SecurityConfigurerAdapter<O, B>> C apply(C configurer) throws Exception{
        configurer.addObjectPostProcessor(objectPostProcessor);
        configurer.setBuilder((B) this);
        add(configurer);
        return configurer;
    }

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

    /**
     * 删除所有通过类名找到的{@link SecurityConfigurer}实例, 如果找不到则返回<code>null</code>。 请注意，不考虑对象层次结构。
     * @param clazz
     * @param <C>
     * @return
     */
    public <C extends SecurityConfigurer<O, B>> C removeConfigurer(Class<C> clazz) {
        List<SecurityConfigurer<O, B>> configs = this.configurers.remove(clazz);
        if (configs == null) {
            return null;
        }
        if (configs.size() != 1) {
            throw new IllegalStateException("Only one configurer expected for type "
                    + clazz + ", but got " + configs);
        }
        return (C) configs.get(0);
    }

    /**
     * 删除所有通过类名找到的{@link SecurityConfigurer}实例, 如果找不到则返回空集合。 请注意，不考虑对象层次结构。
     * @param clazz
     * @param <C>
     * @return
     */
    public <C extends SecurityConfigurer<O, B>> List<C> removeConfigurers(Class<C> clazz) {
        List<C> configs = (List<C>) this.configurers.remove(clazz);
        if (configs == null) {
            return new ArrayList<C>();
        }
        return new ArrayList<C>(configs);
    }



    /**
     * 通过其类名或未找到的空列表获取所有{@link SecurityConfigurer}实例。 请注意，不考虑对象层次结构
     * @param clazz
     * @param <C>
     * @return
     */
    public <C extends SecurityConfigurer<O, B>> List<C> getConfigurers(Class<C> clazz) {
        List<C> configs = (List<C>) this.configurers.get(clazz);
        if (configs == null) {
            return new ArrayList<C>();
        }
        return new ArrayList<C>(configs);
    }


    /**
     * 通过其类名获取{@link SecurityConfigurer}；如果未找到，则获取<code> null </ code>。 请注意，不考虑对象层次结构
     * @param clazz
     * @param <C>
     * @return
     */
    public <C extends SecurityConfigurer<O, B>> C getConfigurer(Class<C> clazz) {
        List<SecurityConfigurer<O, B>> configs = this.configurers.get(clazz);
        if (configs == null) {
            return null;
        }
        if (configs.size() != 1) {
            throw new IllegalStateException("Only one configurer expected for type "
                    + clazz + ", but got " + configs);
        }
        return (C) configs.get(0);
    }


    /**
     * 此处O类型为Filter类型, 由WebSecurity决定
     * @return
     * @throws Exception
     */
    @Override
    protected O doBuild() throws Exception {
        synchronized (configurers){
            buildState = BuildState.INITIALIZING;
            beforeInit();//空实现, 子类重写后可以实现挂接
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

    protected <P> P postProcess(P object){
        return this.objectPostProcessor.postProcess(object);
    }

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
