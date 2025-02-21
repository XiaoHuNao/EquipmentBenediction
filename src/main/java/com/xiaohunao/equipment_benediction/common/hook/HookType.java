package com.xiaohunao.equipment_benediction.common.hook;

import com.mojang.serialization.MapCodec;
import com.xiaohunao.equipment_benediction.common.hook.dynamic.ISerializableHook;
import net.minecraft.resources.ResourceLocation;

public class HookType<T extends IHook> {
    private final ResourceLocation id;
    private final Class<T> hookClass;
    private final boolean isSerializable;

    private HookType(ResourceLocation id, Class<T> hookClass) {
        this.id = id;
        this.hookClass = hookClass;
        this.isSerializable = ISerializableHook.class.isAssignableFrom(hookClass);
    }

    @SuppressWarnings("unchecked")
    private HookType(ResourceLocation id, Class<T> hookClass, MapCodec<T> codec) {
        this(id, hookClass);
        if (!ISerializableHook.class.isAssignableFrom(hookClass)) {
            throw new IllegalArgumentException("Hook class " + hookClass.getName() + " must implement ISerializableHook");
        }
        ISerializableHook.register(id, (Class<? extends ISerializableHook>) hookClass, (MapCodec<? extends ISerializableHook>) codec);
    }

    public ResourceLocation getId() {
        return id;
    }

    public Class<T> getHookClass() {
        return hookClass;
    }

    public boolean isSerializable() {
        return isSerializable;
    }

    @SuppressWarnings("unchecked")
    public MapCodec<? extends ISerializableHook> getCodec() {
        if (!isSerializable) {
            throw new UnsupportedOperationException("Hook type " + id + " is not serializable");
        }
        return ISerializableHook.CLASS_TO_CODEC.get((Class<? extends ISerializableHook>) hookClass);
    }

    public static <T extends IHook> HookType<T> createHook(ResourceLocation id, Class<T> hookClass) {
        return new HookType<>(id, hookClass);
    }

    public static <T extends ISerializableHook> HookType<T> createSerializableHook(ResourceLocation id, Class<T> hookClass, MapCodec<T> codec) {
        return new HookType<>(id, hookClass, codec);
    }
}
