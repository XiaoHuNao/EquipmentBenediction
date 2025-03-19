package com.xiaohunao.equipment_benediction.common.interfaces;

import com.mojang.serialization.Codec;
import com.xiaohunao.equipment_benediction.api.manager.BenedictionManager;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public interface IBenediction{
    BenedictionManager benedictionManager = BenedictionManager.getInstance();
    Codec<IBenediction> CODEC_BY_ID = ResourceLocation.CODEC.xmap(benedictionManager::getBenedictionFromManagers, benedictionManager::getBenedictionIdFromManagers);
}
