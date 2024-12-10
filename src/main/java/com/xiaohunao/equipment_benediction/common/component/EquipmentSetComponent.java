package com.xiaohunao.equipment_benediction.common.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public class EquipmentSetComponent implements DataComponentType<EquipmentSetComponent> {
    @Override
    public @Nullable Codec<EquipmentSetComponent> codec() {
        return null;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, EquipmentSetComponent> streamCodec() {
        return null;
    }
}
