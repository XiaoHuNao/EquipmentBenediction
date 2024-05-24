package com.xiaohunao.equipment_benediction.common.init;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.IEquipmentSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.Map;
import java.util.function.Supplier;

public class ModEquipmentSet {
    public static final Multimap<Ingredient,IEquipmentSet> SET_MAP = ArrayListMultimap.create();
    public static final ResourceKey<Registry<IEquipmentSet>> KEY = EquipmentBenediction.asResourceKey("equipment_set");
    public static final DeferredRegister<IEquipmentSet> EQUIPMENT_SET = DeferredRegister.create(KEY, EquipmentBenediction.MOD_ID);
    public static final Supplier<IForgeRegistry<IEquipmentSet>> REGISTRY = EQUIPMENT_SET.makeRegistry(RegistryBuilder::new);


    public static void setup() {
        ModEquipmentSet.REGISTRY.get().getEntries().stream()
                .map(Map.Entry::getValue)
                .forEach((set) -> {
                    set.getGroup().getEquipages().forEach((equippable,ingredient) -> {
                        ModEquipmentSet.SET_MAP.put(ingredient, set);
                    });
                });
    }

}
