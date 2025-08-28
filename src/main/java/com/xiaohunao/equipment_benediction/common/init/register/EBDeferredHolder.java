package com.xiaohunao.equipment_benediction.common.init.register;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * 延迟持有者，类似于 DeferredHolder
 * @param <T> 要持有的对象类型
 */
public class EBDeferredHolder<T> implements Supplier<T> {
    private final ResourceLocation id;
    private final Supplier<? extends T> supplier;
    protected T value;  // 改为 protected 让子类可以访问

    EBDeferredHolder(ResourceLocation id, Supplier<? extends T> supplier) {
        this.id = id;
        this.supplier = supplier;
    }

    EBDeferredHolder(ResourceLocation id) {
        this.id = id;
        this.supplier = null;
    }

    /**
     * 获取对象ID
     */
    public ResourceLocation getId() {
        return id;
    }

    /**
     * 获取对象实例
     */
    @Override
    public T get() {
        if (supplier == null) {
            throw new IllegalStateException("Cannot get instance of deferred holder without supplier: " + id);
        }
        if (value == null) {
            value = supplier.get();
        }
        return value;
    }
} 