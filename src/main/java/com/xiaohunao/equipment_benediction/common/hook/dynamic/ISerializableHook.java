package com.xiaohunao.equipment_benediction.common.hook.dynamic;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.xiaohunao.equipment_benediction.common.hook.HookType;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.init.EBRegistries;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public interface ISerializableHook extends IHook {
    BiMap<Class<? extends ISerializableHook>, ResourceLocation> CLASS_TO_ID = HashBiMap.create();
    BiMap<Class<? extends ISerializableHook>, MapCodec<? extends ISerializableHook>> CLASS_TO_CODEC = HashBiMap.create();

    Codec<ISerializableHook> CODEC = ResourceLocation.CODEC.dispatch(
            ISerializableHook::getRegistryId,
            id -> CLASS_TO_CODEC.get(CLASS_TO_ID.inverse().get(id))
    );

    default MapCodec<? extends ISerializableHook> codec() {
        return CLASS_TO_CODEC.get(this.getClass());
    }

    default ResourceLocation getRegistryId() {
        ResourceLocation id = CLASS_TO_ID.get(this.getClass());
        if (id == null) {
            throw new IllegalStateException("Hook " + this.getClass().getName() + " is not registered");
        }
        return id;
    }

    static boolean isRegistered(Class<? extends ISerializableHook> hookClass) {
        return CLASS_TO_ID.containsKey(hookClass);
    }

    @Nullable
    static Class<? extends ISerializableHook> getClassById(ResourceLocation id) {
        return CLASS_TO_ID.inverse().get(id);
    }

    static HookType<?> getHookType(Class<? extends ISerializableHook> hookClass) {
        return EBRegistries.HOOK_TYPES.get(CLASS_TO_ID.get(hookClass));
    }

    static void register(ResourceLocation id, Class<? extends ISerializableHook> hookClass, MapCodec<? extends ISerializableHook> codec) {
        CLASS_TO_ID.put(hookClass, id);
        CLASS_TO_CODEC.put(hookClass, codec);
    }
}

