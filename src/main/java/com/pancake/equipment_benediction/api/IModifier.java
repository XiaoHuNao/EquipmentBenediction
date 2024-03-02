package com.pancake.equipment_benediction.api;

import com.mojang.serialization.Codec;
import com.pancake.equipment_benediction.common.bonus.BonusHandler;
import com.pancake.equipment_benediction.common.init.ModModifiers;
import com.pancake.equipment_benediction.common.modifier.ModifierInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.Supplier;

public interface IModifier extends ICodec<IModifier> {
    BonusHandler<ModifierInstance> getHandler();

    Component getDisplayName();
    Component getDescription();

    TextColor getTextColor();

    String getTranslationKey();

    ResourceLocation getRegistryName();


    void apply(Player player);

    void clear(Player player);


}


