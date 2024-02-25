package com.pancake.equipment_benediction.api;

import com.mojang.serialization.Codec;
import com.pancake.equipment_benediction.common.init.ModModifiers;
import com.pancake.equipment_benediction.common.modifier.ModifierHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.Supplier;

public interface IModifier extends ICodec<IModifier>{
    Supplier<Codec<IModifier>> CODEC = () -> ModModifiers.MODIFIER_REGISTRY.get().getCodec()
            .dispatch("modifier", IModifier::type, IModifier::codec);

    void applyDisplayName(List<Component> components);

//    ResourceLocation getIdentifier();

    ModifierHandler getHandler();

    Component getDisplayName();

    TextColor getTextColor();

    String getTranslationKey();
    void registryEvent();

    void unregisterEvent();

    void removeAttributeModifier(Player player);

    void removeEffectModifier(Player player);
    void addEffectModifier(Player player);

    void addAttributeModifier(Player player);

    ResourceLocation getRegistryName();
}


