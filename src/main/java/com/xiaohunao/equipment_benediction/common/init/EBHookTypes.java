package com.xiaohunao.equipment_benediction.common.init;

import com.mojang.serialization.MapCodec;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.hook.HookType;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.hook.dynamic.ISerializableHook;
import com.xiaohunao.equipment_benediction.common.hook.dynamic.KnockbackHook;
import com.xiaohunao.equipment_benediction.common.hook.hooks.BeforeMeleeHitHook;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EBHookTypes {
    public static final DeferredRegister<HookType<?>> HOOK_TYPES = DeferredRegister.create(EBRegistries.Keys.HOOK_TYPES, EquipmentBenediction.MODID);

    public static final DeferredHolder<HookType<?>, HookType<?>> BEFORE_MELEE_HIT = register("before_melee_hit", BeforeMeleeHitHook.class);
    public static final DeferredHolder<HookType<?>, HookType<?>> KNOCKBACK = register("knockback", KnockbackHook.class, KnockbackHook.CODEC);


    public static <T extends IHook> DeferredHolder<HookType<?>, HookType<?>>  register(String id, Class<T> hookClass) {
        return HOOK_TYPES.register(id,() -> HookType.createHook(EquipmentBenediction.asResource(id), hookClass));
    }

    public static <T extends ISerializableHook> DeferredHolder<HookType<?>, HookType<?>>  register(String id, Class<T> hookClass, MapCodec<T> codec) {
        return HOOK_TYPES.register(id, () -> HookType.createSerializableHook(EquipmentBenediction.asResource(id), hookClass, codec));
    }

}
