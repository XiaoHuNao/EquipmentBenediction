package com.xiaohunao.equipment_benediction.api;

import com.xiaohunao.equipment_benediction.common.modifier.ModifierInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.function.Predicate;

public interface IQuality extends ICodec<IQuality>{
    boolean isAutoAdd();

    void addFixedModifier(ModifierInstance... modifier);

    void addFixedModifier(String... modifier);

    void addRandomModifier(ModifierInstance... modifier);

    void addRandomModifier(String... modifier);

    List<ModifierInstance> getRandomModifiers();
    List<ModifierInstance> getFixedModifiers();

    SimpleWeightedRandomList<IModifier> getWeightedModifiers();

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
