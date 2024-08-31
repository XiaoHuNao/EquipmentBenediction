package com.xiaohunao.equipment_benediction.common.init;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class ModEquipmentSet {
    public static final HashBiMap<ResourceLocation, EquipmentSet> SET_MAP = HashBiMap.create();
    public static final Multimap<Ingredient,EquipmentSet> INGREDIENT_MAP = ArrayListMultimap.create();


    public static void register(ResourceLocation key, EquipmentSet set) {
        SET_MAP.put(key, set);
        set.group.equippables.forEach((equippable,ingredient) -> {
            INGREDIENT_MAP.put(ingredient, set);
        });
    }



}
