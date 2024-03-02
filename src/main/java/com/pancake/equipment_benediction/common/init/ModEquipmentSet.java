package com.pancake.equipment_benediction.common.init;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.api.IEquipmentSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.Map;
import java.util.function.Supplier;

public class ModEquipmentSet {
    public static final Multimap<Ingredient,IEquipmentSet> EQUIPMENT_SET_MAP = ArrayListMultimap.create();
    public static final ResourceKey<Registry<IEquipmentSet>> EQUIPMENT_SET_KEY = EquipmentBenediction.asResourceKey("equipment_set");
    public static final DeferredRegister<IEquipmentSet> EQUIPMENT_SET = DeferredRegister.create(EQUIPMENT_SET_KEY, EquipmentBenediction.MOD_ID);
    public static final Supplier<IForgeRegistry<IEquipmentSet>> EQUIPMENT_SET_REGISTRY = EQUIPMENT_SET.makeRegistry(RegistryBuilder::new);

//    public static final RegistryObject<IEquipmentSet> DIAMOND_SET = EQUIPMENT_SET.register("diamond_set", DiamondSet::new);



    public static void setup() {
        ModEquipmentSet.EQUIPMENT_SET_REGISTRY.get().getEntries().stream()
                .map(Map.Entry::getValue)
                .forEach((set) -> {
                    set.getGroup().getEquipages().forEach((equippable,ingredient) -> {
                        ModEquipmentSet.EQUIPMENT_SET_MAP.put(ingredient, set);
                    });
                });
    }

}
