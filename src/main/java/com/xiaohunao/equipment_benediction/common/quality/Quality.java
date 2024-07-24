package com.xiaohunao.equipment_benediction.common.quality;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.xiaohunao.equipment_benediction.api.IModifier;
import com.xiaohunao.equipment_benediction.api.IQuality;
import com.xiaohunao.equipment_benediction.common.init.ModQuality;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierInstance;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public abstract class Quality implements IQuality {
    protected final List<ModifierInstance> modifiers = Lists.newArrayList();
    protected final List<ModifierInstance> randomModifiers = Lists.newArrayList();
    protected final List<ModifierInstance> fixedModifier = Lists.newArrayList();
    private final Ingredient recastingStack;
    private final int rarity;
    private final int level;
    private final int maxModifierCount;
    protected Predicate<ItemStack> isViable = stack -> true;

    protected int color = 0x7a7b78;
    private MutableComponent displayName;
    private String translationKey;
    protected boolean autoAdd = false;

    public Quality(Ingredient recastingStack, int rarity, int level, int maxModifierCount) {
        this.recastingStack = recastingStack;
        this.rarity = rarity;
        this.level = level;
        this.maxModifierCount = maxModifierCount;
    }

    @Override
    public boolean isAutoAdd() {
        return autoAdd;
    }

    public void setAutoAdd(boolean autoAdd) {
        this.autoAdd = autoAdd;
    }

    public void setViable(Predicate<ItemStack> isViable) {
        this.isViable = isViable;
    }

    @Override
    public void addFixedModifier(ModifierInstance... modifier) {
        int count = maxModifierCount - this.modifiers.size();
        this.fixedModifier.addAll(Arrays.asList(modifier).subList(0, Math.min(count, modifier.length)));
    }

    @Override
    public void addFixedModifier(String... modifier) {
        final int count = maxModifierCount - this.modifiers.size();
        List<ModifierInstance> list = Arrays.stream(modifier).map(ModifierInstance::new).toList();
        this.fixedModifier.addAll(list.subList(0, Math.min(count, list.size())));
    }

    @Override
    public void addRandomModifier(ModifierInstance... modifier) {
        this.randomModifiers.addAll(Lists.newArrayList(modifier));
    }

    @Override
    public void addRandomModifier(String... modifier) {
        List<ModifierInstance> list = Arrays.stream(modifier).map(ModifierInstance::new).toList();
        this.randomModifiers.addAll(list);
    }

    @Override
    public List<ModifierInstance> getRandomModifiers() {
        return randomModifiers;
    }

    @Override
    public List<ModifierInstance> getFixedModifiers() {
        return fixedModifier;
    }

    @Override
    public SimpleWeightedRandomList<IModifier> getWeightedModifiers() {
        SimpleWeightedRandomList.Builder<IModifier> builder = SimpleWeightedRandomList.builder();
        randomModifiers.forEach((modifierInstance) -> {
            builder.add(modifierInstance.getModifier(), modifierInstance.getModifier().getRarity());
        });
        return builder.build();
    }

    @Override
    public Component getDisplayName() {
        if (displayName == null) {
            displayName = Component.translatable(getTranslationKey()).withStyle(style -> style.withColor(getColor()));
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
    public final TextColor getColor() {
        return TextColor.fromRgb(color);
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
