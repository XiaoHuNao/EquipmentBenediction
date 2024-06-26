package com.xiaohunao.equipment_benediction.api;

import com.xiaohunao.equipment_benediction.common.modifier.ModifierInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.function.Predicate;

public interface IQuality extends ICodec<IQuality>{
    void addFixedModifier(ModifierInstance modifier);

    Component getDisplayName();

    String getTranslationKey();

    ResourceLocation getRegistryName();

    TextColor getColor();

    List<ModifierInstance> getModifiers();

    int getRarity();

    Ingredient getRecastingStack();

    int getLevel();

    int getMaxModifierCount();

    Predicate<ItemStack> isViable();
}
