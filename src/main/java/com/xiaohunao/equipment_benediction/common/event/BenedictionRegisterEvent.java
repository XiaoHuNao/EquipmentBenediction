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
    public void registerEquipmentSet(ResourceLocation key) {
        this.registerEquipmentSet(new ResourceLocation[]{key},equipmentSet -> {});
    }
    public void registerEquipmentSet(ResourceLocation key,Consumer<EquipmentSet> consumer) {
        this.registerEquipmentSet(new ResourceLocation[]{key},consumer);
    }

    public void registerEquipmentSet(ResourceLocation[] key,Consumer<EquipmentSet> consumer) {
        for (ResourceLocation location : key) {
            EquipmentSet equipmentSet = EquipmentSet.create(location);
            consumer.accept(equipmentSet);
            ModEquipmentSet.register(location, equipmentSet);
        }
    }

    public void registerModifier(ResourceLocation key) {
        this.registerModifier(new ResourceLocation[]{key},modifier -> {});
    }
    public void registerModifier(ResourceLocation key,Consumer<Modifier> consumer) {
        this.registerModifier(new ResourceLocation[]{key},consumer);
    }

    public void registerModifier(ResourceLocation[] key,Consumer<Modifier> consumer) {
        for (ResourceLocation location : key) {
            Modifier modifier = Modifier.create(location);
            consumer.accept(modifier);
            ModModifier.register(location, modifier);
        }
    }

    public void registerQuality(ResourceLocation key) {
        this.registerQuality(new ResourceLocation[]{key},quality -> {});
    }
    public void registerQuality(ResourceLocation key,Consumer<Quality> consumer) {
        this.registerQuality(new ResourceLocation[]{key},consumer);
    }
    public void registerQuality(ResourceLocation[] key, Consumer<Quality> consumer) {
        for (ResourceLocation location : key) {
            Quality quality = Quality.create(location);
            consumer.accept(quality);
            ModQuality.register(location, quality);
        }
    }
    public void modifyEquipmentSet(ResourceLocation key, Consumer<EquipmentSet> consumer) {
        this.modifyEquipmentSet(new ResourceLocation[]{key},consumer);
    }
    public void modifyModifier(ResourceLocation key, Consumer<Modifier> consumer) {
        this.modifyModifier(new ResourceLocation[]{key},consumer);
    }
    public void modifyQuality(ResourceLocation key, Consumer<Quality> consumer) {
        this.modifyQuality(new ResourceLocation[]{key},consumer);
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
