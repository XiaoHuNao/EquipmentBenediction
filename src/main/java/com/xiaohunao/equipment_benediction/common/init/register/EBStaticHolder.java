package com.xiaohunao.equipment_benediction.common.init.register;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class EBStaticHolder<T> extends EBDeferredHolder<T> {
    EBStaticHolder(ResourceLocation id, Supplier<? extends T> supplier) {
        super(id, supplier);
    }
} 