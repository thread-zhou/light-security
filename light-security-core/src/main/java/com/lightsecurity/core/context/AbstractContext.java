package com.lightsecurity.core.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Context接口的基本通用实现
 * @param <K>
 * @param <V>
 */
public abstract class AbstractContext<K, V> implements Context<K, V> {

    private final ConcurrentHashMap<K, V> context =  new ConcurrentHashMap<>();//该容器类的默认实现
    private static final ConcurrentHashMap<String, AbstractContext> instanceMap = new ConcurrentHashMap<>();//放置子类对象，子类单例的存放容器
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected AbstractContext() {
        logger.info("实际实例化全类名：{}", this.getClass().getName());
        getInstance(this);
        logger.info("目前一共存在 {} 个子类实例对象, 具体如下", instanceMap.size());
        instanceMap.forEach((k, v) -> logger.info("实例全类名: {}, 实例对象打印信息：{} ", k, v.getContextCount()));
    }

    /**
     * 双检锁，控制子类实例都是单例（参考极客时间 Java核心技术36将第14讲）
     * 其实可以不需要返回值
     * @param instance
     * @return
     */
    protected AbstractContext<K, V> getInstance(AbstractContext instance){
        logger.info("当前对应实例：{}", instanceMap.get(instance.getClass().getName()));
        if (instanceMap.get(instance.getClass().getName()) == null){
            synchronized (AbstractContext.class){
                if (instanceMap.get(instance.getClass().getName()) == null){
                    instanceMap.put(instance.getClass().getName(), instance);
                }
            }
        }
        return instanceMap.get(instance.getClass().getName());
    }

    /**
     * 返回承载子类实例的Map对象
     * @return
     */
    protected ConcurrentHashMap<String, AbstractContext> getInstanceMap(){
        return instanceMap;
    }


    @Override
    public void put(K k, V v) {
        context.put(k, v);
    }

    @Override
    public V get(K k) {
        return context.get(k);
    }

    @Override
    public void remove(K k) {
        context.remove(k);
    }

    @Override
    public void clearContext() {
        context.clear();
    }

    @Override
    public int getContextCount() {
        return context.size();
    }

   /* @Override
    public ConcurrentHashMap<K, V> getContext() {
        return context;
    }*/
}
