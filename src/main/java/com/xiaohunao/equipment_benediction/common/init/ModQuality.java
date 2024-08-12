package com.xiaohunao.equipment_benediction.common.init;

import com.google.common.collect.HashBiMap;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.quality.Quality;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;

public class ModQuality {
    public static final HashBiMap<ResourceLocation, Quality> QUALITY_MAP = HashBiMap.create();
    private static final SimpleWeightedRandomList.Builder<Quality> builder = SimpleWeightedRandomList.builder();

    public static void register(ResourceLocation key, Quality quality) {
        if (!QUALITY_MAP.containsKey(key) && !QUALITY_MAP.containsValue(quality)) {
            QUALITY_MAP.put(key, quality);
            builder.add(quality, quality.getRarity());
        }
        EquipmentBenediction.LOGGER.error("Quality with key {} already exists!", key);
    }

    public static Quality getQuality(ResourceLocation registryName) {
        return QUALITY_MAP.get(registryName);
    }

    public SimpleWeightedRandomList<Quality> getQualityWeightedList() {
        return builder.build();
    }
}
