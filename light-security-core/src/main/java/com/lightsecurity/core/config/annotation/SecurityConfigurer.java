package com.lightsecurity.core.config.annotation;

/**
 * 配置器顶层接口
 * @param <O> 构建器中泛型类型
 * @param <B> 构建器的子类类型
 */
public interface SecurityConfigurer<O, B extends SecurityBuilder<O>> {

    /**
     * 初始化{@link SecurityBuilder}, 并没有进行相关属性的构建
     * @param builder
     * @throws Exception
     */
    void init(B builder) throws Exception;

    /**
     * 根据构建器{@link SecurityBuilder}必要的属性进行配置
     * @param builder
     * @throws Exception
     */
    void configure(B builder) throws Exception;

}
