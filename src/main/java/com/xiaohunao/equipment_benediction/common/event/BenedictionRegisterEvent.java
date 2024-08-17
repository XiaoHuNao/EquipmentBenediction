package com.xiaohunao.equipment_benediction.common.event;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.init.ModEquipmentSet;
import com.xiaohunao.equipment_benediction.common.init.ModModifier;
import com.xiaohunao.equipment_benediction.common.init.ModQuality;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import com.xiaohunao.equipment_benediction.common.quality.Quality;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.Consumer;

public class BenedictionRegisterEvent extends Event {
    public void registerEquipmentSet(ResourceLocation key, Consumer<EquipmentSet> consumer){
        EquipmentSet equipmentSet = EquipmentSet.create(key);
        consumer.accept(equipmentSet);
        ModEquipmentSet.register(key,equipmentSet);
    }
    public void registerModifier(ResourceLocation key, Consumer<Modifier> consumer) {
        Modifier modifier = Modifier.create(key);
        consumer.accept(modifier);
        ModModifier.register(key,modifier);
    }
    public void registerQuality(ResourceLocation key, Consumer<Quality> consumer) {
        Quality quality = Quality.create(key);
        consumer.accept(quality);
        ModQuality.register(key,quality);
    }
}
