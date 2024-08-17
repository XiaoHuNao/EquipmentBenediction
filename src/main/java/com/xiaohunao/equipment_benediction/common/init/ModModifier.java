package com.xiaohunao.equipment_benediction.common.init;

import com.google.common.collect.HashBiMap;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;

public class ModModifier {
    public static final HashBiMap<ResourceLocation, Modifier> MODIFIER_MAP = HashBiMap.create();
    private static final SimpleWeightedRandomList.Builder<Modifier> builder = SimpleWeightedRandomList.builder();

    public static void register(ResourceLocation key, Modifier modifier) {
        MODIFIER_MAP.put(key, modifier);
        builder.add(modifier, modifier.getRarity());
    }
    public static SimpleWeightedRandomList<Modifier> getWeightedList() {
        return builder.build();
    }
}
