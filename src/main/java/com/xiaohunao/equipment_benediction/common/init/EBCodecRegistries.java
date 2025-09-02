package com.xiaohunao.equipment_benediction.common.init;

import com.mojang.serialization.MapCodec;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.equippable.IWearable;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaWearable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EBCodecRegistries {
    public static final DeferredRegister<MapCodec<? extends IWearable>> EQUIPPABLE_CODEC = DeferredRegister.create(EBRegistries.Keys.EQUIPPABLE_CODEC, EquipmentBenediction.MODID);

    public static final DeferredHolder<MapCodec<? extends IWearable>, MapCodec<? extends IWearable>> VANILLA_EQUIPPABLE = EQUIPPABLE_CODEC.register("vanilla", () -> VanillaWearable.CODEC);


    public static void init(IEventBus eventBus) {
        EQUIPPABLE_CODEC.register(eventBus);
    }
}
