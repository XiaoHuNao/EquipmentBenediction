package com.xiaohunao.equipment_benediction.common.init;

import com.mojang.serialization.MapCodec;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.verifier.IVerifier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.Objects;
import java.util.function.Supplier;

public class EQRegistries {
    public static final Registry<MapCodec<? extends IVerifier>> VERIFIER_CODEC = new RegistryBuilder<>(Keys.VERIFIER_CODEC).create();

    public static final class Keys {
        public static final ResourceKey<Registry<MapCodec<? extends IVerifier>>> VERIFIER_CODEC = EquipmentBenediction.asResourceKey("verifier_codec");
    }

    public static final class Suppliers {
        public static final Supplier<Registry<MapCodec<? extends IVerifier>>> VERIFIER_CODEC = supplyRegistry(Keys.VERIFIER_CODEC);

    }

    static <T> Supplier<T> supplyRegistry(ResourceKey<T> key) {
        return com.google.common.base.Suppliers.memoize(() -> Objects.requireNonNull((T) BuiltInRegistries.REGISTRY.get((ResourceKey) key)));
    }

    public static void registerRegistries(NewRegistryEvent event) {
        event.register(VERIFIER_CODEC);
    }
}
