package com.xiaohunao.equipment_benediction.compat.kubejs;


import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.init.ModEquipmentSet;
import com.xiaohunao.equipment_benediction.common.init.ModModifier;
import com.xiaohunao.equipment_benediction.common.init.ModQuality;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import com.xiaohunao.equipment_benediction.common.quality.Quality;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;


public class BenedictionRegisterEventJS extends EventJS {
    public void registerEquipmentSet(ResourceLocation[] key) {
        this.registerEquipmentSet(key,equipmentSet -> {});
    }

    public void registerEquipmentSet(ResourceLocation[] key,Consumer<EquipmentSet> consumer) {
        for (ResourceLocation location : key) {
            EquipmentSet equipmentSet = EquipmentSet.create(location);
            consumer.accept(equipmentSet);
            ModEquipmentSet.register(location, equipmentSet);
        }
    }

    public void registerModifier(ResourceLocation[] key) {
        this.registerModifier(key,modifier -> {});
    }

    public void registerModifier(ResourceLocation[] key,Consumer<Modifier> consumer) {
        for (ResourceLocation location : key) {
            Modifier modifier = Modifier.create(location);
            consumer.accept(modifier);
            ModModifier.register(location, modifier);
        }
    }

    public void registerQuality(ResourceLocation[] key) {
        this.registerQuality(key, quality -> {
        });
    }

    public void registerQuality(ResourceLocation[] key, Consumer<Quality> consumer) {
        for (ResourceLocation location : key) {
            Quality quality = Quality.create(location);
            consumer.accept(quality);
            ModQuality.register(location, quality);
        }
    }

    public void modifyEquipmentSet(ResourceLocation[] key, Consumer<EquipmentSet> consumer) {
        for (ResourceLocation location : key) {
            EquipmentSet equipmentSet = ModEquipmentSet.SET_MAP.get(location);
            consumer.accept(equipmentSet);
            ModEquipmentSet.register(location, equipmentSet);
        }
    }
    public void modifyModifier(ResourceLocation[] key, Consumer<Modifier> consumer) {
        for (ResourceLocation location : key) {
            Modifier modifier = ModModifier.MODIFIER_MAP.get(location);
            consumer.accept(modifier);
            ModModifier.register(location, modifier);
        }
    }
    public void modifyQuality(ResourceLocation[] key, Consumer<Quality> consumer) {
        for (ResourceLocation location : key) {
            Quality quality = ModQuality.QUALITY_MAP.get(location);
            consumer.accept(quality);
            ModQuality.register(location, quality);
        }
    }
}
