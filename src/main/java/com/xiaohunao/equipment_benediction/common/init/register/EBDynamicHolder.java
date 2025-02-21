package com.xiaohunao.equipment_benediction.common.init.register;

import com.xiaohunao.equipment_benediction.common.manager.EBAbstractManager;
import net.minecraft.resources.ResourceLocation;
import java.util.concurrent.atomic.AtomicInteger;

public class EBDynamicHolder<T> extends EBDeferredHolder<T> {
    private static final AtomicInteger INVALIDATION_COUNTER = new AtomicInteger(0);
    private int invalidationCount = -1;
    private final EBAbstractManager<T> manager;

    EBDynamicHolder(ResourceLocation id, EBAbstractManager<T> manager) {
        super(id);
        this.manager = manager;
    }

    @Override
    public T get() {
        if (invalidationCount < INVALIDATION_COUNTER.get()) {
            value = null;
        }
        if (value == null) {
            value = manager.getDynamicResource(getId());
            if (value == null) {
                throw new IllegalStateException("Dynamic resource not found: " + getId());
            }
            invalidationCount = INVALIDATION_COUNTER.get();
        }
        return value;
    }

    public static void invalidateAll() {
        INVALIDATION_COUNTER.incrementAndGet();
    }
} 