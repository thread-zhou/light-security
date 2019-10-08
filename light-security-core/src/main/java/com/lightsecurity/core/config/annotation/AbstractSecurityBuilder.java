package com.lightsecurity.core.config.annotation;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractSecurityBuilder<O> implements SecurityBuilder<O> {

    private AtomicBoolean building = new AtomicBoolean();

    private O object;

    public final O build() throws Exception{
        //compareAndSet：如果当前值 == 预期值，则以原子方式将该值设置为给定的更新值
        if (this.building.compareAndSet(false, true)){
            this.object = doBuild();
            return this.object;
        }
        throw new AlreadyBuiltException("This object has already been built");
    }

    /**
     * 获取已经构建好的对象, 如果没有构建好则抛出异常
     * @return
     */
    public final O getObject(){
        if (!this.building.get()){
            throw new IllegalStateException("This object has not been built");
        }
        return this.object;
    }

    /**
     * 由子类实现该功能进行构建
     * @return
     * @throws Exception
     */
    protected abstract O doBuild() throws Exception;

}
