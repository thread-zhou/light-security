package com.lightsecurity.core.context;

public interface ContextHolderStrategy<K, V> {

    /**
     * 获取当前容器
     * @return
     */
    Context<K, V> getContextWrapper();

    /**
     * 在存储前剔除某些数据
     * @param k
     * @param v
     */
    void put(K k, V v);


}
