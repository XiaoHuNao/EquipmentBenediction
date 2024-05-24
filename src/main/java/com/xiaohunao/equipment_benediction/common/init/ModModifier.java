package com.xiaohunao.equipment_benediction.common.init;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.IModifier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.Map;
import java.util.function.Supplier;

public class ModModifier {
    public static SimpleWeightedRandomList<IModifier> WEIGHTED_MODIFIER;
    public static final ResourceKey<Registry<IModifier>> KEY = EquipmentBenediction.asResourceKey("modifier");
    public static final DeferredRegister<IModifier> MODIFIER = DeferredRegister.create(KEY, EquipmentBenediction.MOD_ID);
    public static final Supplier<IForgeRegistry<IModifier>> REGISTRY = MODIFIER.makeRegistry(RegistryBuilder::new);

    public static void setup() {
        SimpleWeightedRandomList.Builder<IModifier> builder = SimpleWeightedRandomList.builder();
        REGISTRY.get().getEntries().stream()
                .map(Map.Entry::getValue)
                .forEach((modifier) -> {
                    builder.add(modifier, modifier.getRarity());
                });
        WEIGHTED_MODIFIER = builder.build();
    }
}
