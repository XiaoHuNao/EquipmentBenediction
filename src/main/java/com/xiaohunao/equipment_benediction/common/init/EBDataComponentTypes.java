package com.xiaohunao.equipment_benediction.common.init;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.component.ModifierComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EBDataComponentTypes {
    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPE = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, EquipmentBenediction.MODID);

    public static final DeferredHolder<DataComponentType<?>,DataComponentType<ModifierComponent>> MODIFIER = DATA_COMPONENT_TYPE.registerComponentType("modifier",
            builder -> builder.persistent(ModifierComponent.CODEC).networkSynchronized(ModifierComponent.STREAM_CODEC));
}
