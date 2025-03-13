package com.xiaohunao.equipment_benediction.common.init;

import com.mojang.serialization.MapCodec;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.equippable.IEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.Objects;
import java.util.function.Supplier;


public class EBRegistries {
    public static final Registry<HookType<?>> HOOK_TYPES = new RegistryBuilder<>(Keys.HOOK_TYPES).create();
    public static final Registry<MapCodec<? extends IEquippable>> EQUIPPABLE_CODEC = new RegistryBuilder<>(Keys.EQUIPPABLE_CODEC).create();


    public static class Keys {
        public static final ResourceKey<Registry<HookType<?>>> HOOK_TYPES = EquipmentBenediction.asResourceKey("hook_type");
        public static final ResourceKey<Registry<MapCodec<? extends IEquippable>>> EQUIPPABLE_CODEC = EquipmentBenediction.asResourceKey("equippable");
    }

    public static final class Suppliers {
        public static final Supplier<Registry<HookType<?>>> HOOK_TYPES = supplyRegistry(Keys.HOOK_TYPES);
        public static final Supplier<Registry<MapCodec<? extends IEquippable>>> EQUIPPABLE_CODEC = supplyRegistry(Keys.EQUIPPABLE_CODEC);
    }

    public static void registerRegistries(NewRegistryEvent event) {
        event.register(HOOK_TYPES);
        event.register(EQUIPPABLE_CODEC);
    }

    static <T> Supplier<T> supplyRegistry(ResourceKey<T> key) {
        return com.google.common.base.Suppliers.memoize(() -> Objects.requireNonNull((T) BuiltInRegistries.REGISTRY.get((ResourceKey) key)));
    }

}
