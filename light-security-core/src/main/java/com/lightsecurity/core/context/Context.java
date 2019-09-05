package com.lightsecurity.core.context;

/**
 * 容器基础接口
 * @param <K>
 * @param <V>
 */
public interface Context<K, V> {

    /**
     * 新增
     * @param k
     * @param v
     */
    void put(K k, V v);

    /**
     * 获取指定k值的内容
     * @param k
     * @return
     */
    V get(K k);

    /**
     * 删除指定k值得内容
     * @param k
     */
    void remove(K k);

    /**
     * 清空容器
     */
    void clearContext();

    /**
     * 这里返回的size可能与实际的情况存在偏差（多线程场景下）
     * @return
     */
    int getContextCount();

    /**
     * 获取到真实容器对象，默认实现为ConcurrentHashMap<K, V>
     * @return
     */
//    Map<K, V> getContext();


}
