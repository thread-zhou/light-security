package com.lightsecurity.core.config.annotation;

/**
 * 构建器顶层接口
 * @param <O>
 */
public interface SecurityBuilder <O> {

    /**
     * 尝试构建对象并返回, 可能会返回null
     * @return
     * @throws Exception
     */
    O build() throws Exception;

}
