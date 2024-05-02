package com.pancake.equipment_benediction.common.quality;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.pancake.equipment_benediction.api.IQuality;
import com.pancake.equipment_benediction.common.init.ModQuality;
import com.pancake.equipment_benediction.common.modifier.ModifierInstance;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.function.Predicate;

public class Quality implements IQuality {
    protected final List<ModifierInstance> modifiers = Lists.newArrayList();
    private final Ingredient recastingStack;
    private final int rarity;
    private final int level;
    private final int maxModifierCount;
    protected Predicate<ItemStack> isViable = ItemStack::isDamageableItem;

    protected int textColor = 0x7a7b78;
    private MutableComponent displayName;
    private String translationKey;

    public Quality(Ingredient recastingStack, int rarity, int level, int maxModifierCount) {
        this.recastingStack = recastingStack;
        this.rarity = rarity;
        this.level = level;
        this.maxModifierCount = maxModifierCount;
    }



    public void setViable(Predicate<ItemStack> isViable) {
        this.isViable = isViable;
    }

    @Override
    public void addFixedModifier(ModifierInstance modifier) {
        int size = this.modifiers.size();
        if (size >= maxModifierCount) return;
        this.modifiers.add(modifier);
    }


    @Override
    public Component getDisplayName() {
        if (displayName == null) {
            displayName = Component.translatable(getTranslationKey()).withStyle(style -> style.withColor(getTextColor()));
        }
        return displayName;
    }


    @Override
    public final String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.makeDescriptionId("quality", getRegistryName());
        }

        return this.translationKey;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ModQuality.REGISTRY.get().getKey(this);
    }

    @Override
    public final TextColor getTextColor() {
        return TextColor.fromRgb(textColor);
    }

    @Override
    public Codec<IQuality> codec() {
        return Codec.unit(() -> ModQuality.REGISTRY.get().getValue(getRegistryName()));
    }

    @Override
    public IQuality type() {
        return ModQuality.REGISTRY.get().getValue(getRegistryName());
    }

    @Override
    public List<ModifierInstance> getModifiers() {
        return modifiers;
    }
    @Override
    public int getRarity() {
        return rarity;
    }

    @Override
    public Ingredient getRecastingStack() {
        return recastingStack;
    }
    @Override
    public int getLevel() {
        return level;
    }
    @Override
    public int getMaxModifierCount() {
        return maxModifierCount;
    }

    @Override
    public Predicate<ItemStack> isViable() {
        return isViable;
    }
}
