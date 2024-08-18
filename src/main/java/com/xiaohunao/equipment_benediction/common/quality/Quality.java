package com.xiaohunao.equipment_benediction.common.quality;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetHelper;
import com.xiaohunao.equipment_benediction.common.init.ModQuality;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierHelper;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierInstance;
import dev.latvian.mods.rhino.Context;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Quality{
    public static final Codec<Quality> CODEC = Codec.STRING.xmap(ResourceLocation::tryParse, ResourceLocation::toString)
            .xmap(QualityHelper::getQuality, Quality::getRegistryName);


    protected final List<ModifierInstance> modifiers = Lists.newArrayList();
    protected final List<ModifierInstance> randomModifiers = Lists.newArrayList();
    protected final List<ModifierInstance> fixedModifier = Lists.newArrayList();
    private Ingredient recastingStack = Ingredient.EMPTY;
    private int rarity = 1;
    private int level = 1;
    private int maxModifierCount = 1;
    private final ResourceLocation registryName;
    protected Predicate<ItemStack> isViable = stack -> true;

    protected int color = 0x7a7b78;
    private MutableComponent displayName;
    private String translationKey;
    protected boolean autoAdd = false;

    private Quality(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    public static Quality create(ResourceLocation registryName) {
        return new Quality(registryName);
    }
    public Quality properties(Ingredient recastingStack, int rarity, int level, int maxModifierCount) {
        this.recastingStack = recastingStack;
        this.rarity = rarity;
        this.level = level;
        this.maxModifierCount = maxModifierCount;
        return this;
    }

    public Quality setRecastingStack(Ingredient recastingStack) {
        this.recastingStack = recastingStack;
        return this;
    }

    public Quality setColor(int color) {
        this.color = color;
        return this;
    }

    public Quality setAutoAdd(boolean autoAdd) {
        this.autoAdd = autoAdd;
        return this;
    }

    public Quality setViable(Predicate<ItemStack> isViable) {
        this.isViable = isViable;
        return this;
    }
    public Quality addFixedModifier(ModifierInstance... modifier) {
        int count = maxModifierCount - this.modifiers.size();
        this.fixedModifier.addAll(Arrays.asList(modifier).subList(0, Math.min(count, modifier.length)));
        return this;
    }

    public Quality addFixedModifier(Modifier... modifier) {
        final int count = maxModifierCount - this.modifiers.size();
        List<ModifierInstance> list = Arrays.stream(modifier).map(ModifierInstance::new).toList();
        this.fixedModifier.addAll(list.subList(0, Math.min(count, list.size())));
        return this;
    }

    public Quality addRandomModifier(ModifierInstance... modifier) {
        this.randomModifiers.addAll(Lists.newArrayList(modifier));
        return this;
    }

    public Quality addRandomModifier(Modifier... modifier) {
        List<ModifierInstance> list = Arrays.stream(modifier).map(ModifierInstance::new).toList();
        this.randomModifiers.addAll(list);
        return this;
    }

    public List<ModifierInstance> getRandomModifiers() {
        return randomModifiers;
    }
    public boolean isAutoAdd() {
        return autoAdd;
    }
    public List<ModifierInstance> getFixedModifiers() {
        return fixedModifier;
    }
    public SimpleWeightedRandomList<Modifier> getWeightedModifiers() {
        SimpleWeightedRandomList.Builder<Modifier> builder = SimpleWeightedRandomList.builder();
        randomModifiers.forEach((modifierInstance) -> {
            builder.add(modifierInstance.getModifier(), modifierInstance.getModifier().getRarity());
        });
        return builder.build();
    }

    public Component getDisplayName() {
        if (displayName == null) {
            displayName = Component.translatable(getTranslationKey()).withStyle(style -> style.withColor(getColor()));
        }
        return displayName;
    }

    public final String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.makeDescriptionId("quality", getRegistryName());
        }

        return this.translationKey;
    }

    public ResourceLocation getRegistryName() {
        return ModQuality.QUALITY_MAP.inverse().get(this);
    }

    public final TextColor getColor() {
        return TextColor.fromRgb(color);
    }

    public List<ModifierInstance> getModifiers() {
        return modifiers;
    }
    public int getRarity() {
        return rarity;
    }

    public Ingredient getRecastingStack() {
        return recastingStack;
    }
    public int getLevel() {
        return level;
    }
    public int getMaxModifierCount() {
        return maxModifierCount;
    }

    public Predicate<ItemStack> isViable() {
        return isViable;
    }

    public static Quality wrap(Context context, Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Quality) {
            return (Quality) object;
        } else if (object instanceof ResourceLocation) {
            return ModQuality.getQuality((ResourceLocation) object);
        }else if (object instanceof String) {
            return ModQuality.getQuality(ResourceLocation.tryParse((String) object));
        }else {
            throw new IllegalArgumentException("Cannot convert object to Quality: " + object);
        }
    }
}
