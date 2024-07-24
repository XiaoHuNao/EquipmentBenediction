package com.xiaohunao.equipment_benediction.common.init;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.IModifier;
import com.xiaohunao.equipment_benediction.api.IQuality;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.Map;
import java.util.function.Supplier;

public class ModQuality {
    public static SimpleWeightedRandomList<IQuality> WEIGHTED_QUALITY;
    public static final ResourceKey<Registry<IQuality>> KEY = EquipmentBenediction.asResourceKey("quality");
    public static final DeferredRegister<IQuality> QUALITY = DeferredRegister.create(KEY, EquipmentBenediction.MOD_ID);
    public static final Supplier<IForgeRegistry<IQuality>> REGISTRY = QUALITY.makeRegistry(RegistryBuilder::new);



    public static void setup() {
        SimpleWeightedRandomList.Builder<IQuality> builder = SimpleWeightedRandomList.builder();
        REGISTRY.get().getEntries().stream()
                .map(Map.Entry::getValue)
                .forEach((quality) -> {
                    builder.add(quality, quality.getRarity());
                });
        WEIGHTED_QUALITY = builder.build();
    }




}
