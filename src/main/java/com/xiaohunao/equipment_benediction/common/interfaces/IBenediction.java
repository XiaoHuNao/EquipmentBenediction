package com.xiaohunao.equipment_benediction.common.interfaces;

import com.mojang.serialization.Codec;
import com.xiaohunao.equipment_benediction.api.BenedictionManager;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public interface IBenediction<T extends Tag> extends ISerializable<T> {
    BenedictionManager benedictionManager = BenedictionManager.getInstance();
    Codec<IBenediction<?>> CODEC_BY_ID = ResourceLocation.CODEC.xmap(benedictionManager::getBenedictionFromManagers, benedictionManager::getBenedictionIdFromManagers);
}
