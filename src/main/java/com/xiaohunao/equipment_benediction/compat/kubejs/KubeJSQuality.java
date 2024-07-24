package com.xiaohunao.equipment_benediction.compat.kubejs;

import com.google.common.collect.Lists;
import com.xiaohunao.equipment_benediction.api.IQuality;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierInstance;
import com.xiaohunao.equipment_benediction.common.quality.Quality;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class KubeJSQuality extends Quality {
    public KubeJSQuality(Builder builder) {
        super(builder.recastingStack, builder.rarity, builder.level, builder.maxModifierCount);
        this.isViable = builder.isViable;
        this.modifiers.addAll(builder.modifiers);
        this.randomModifiers.addAll(builder.randomModifiers);
        this.autoAdd = builder.autoAdd;
        this.color = builder.color;
    }

    public static class Builder extends BuilderBase<IQuality> {
        private final List<ModifierInstance> modifiers = Lists.newArrayList();
        private final List<ModifierInstance> randomModifiers = Lists.newArrayList();
        private final List<ModifierInstance> fixedModifier = Lists.newArrayList();
        private Predicate<ItemStack> isViable = stack -> true;
        private Ingredient recastingStack;
        private int rarity;
        private int level;
        private int maxModifierCount;
        private boolean autoAdd = false;
        private int color = 0x7a7b78;

        public Builder(ResourceLocation location) {
            super(location);
        }

        public Builder recastingStack(Ingredient recastingStack) {
            this.recastingStack = recastingStack;
            return this;
        }

        public Builder isAutoAdd(boolean autoAdd) {
            this.autoAdd = autoAdd;
            return this;
        }

        public Builder properties(int rarity, int level, int maxModifierCount) {
            this.rarity = rarity;
            this.level = level;
            this.maxModifierCount = maxModifierCount;
            return this;
        }

        public Builder isViable(Predicate<ItemStack> isViable) {
            this.isViable = isViable;
            return this;
        }
        public Builder addFixedModifier(ModifierInstance... modifier) {
            int count = maxModifierCount - this.modifiers.size();
            this.fixedModifier.addAll(Arrays.asList(modifier).subList(0, Math.min(count, modifier.length)));
            return this;
        }

        public Builder addFixedModifier(String... modifier) {
            final int count = maxModifierCount - this.modifiers.size();
            List<ModifierInstance> list = Arrays.stream(modifier).map(ModifierInstance::new).toList();
            this.fixedModifier.addAll(list.subList(0, Math.min(count, list.size())));
            return this;
        }

        public Builder addRandomModifier(ModifierInstance... modifier) {
            this.randomModifiers.addAll(Lists.newArrayList(modifier));
            return this;
        }

        public Builder addRandomModifier(String... modifier) {
            List<ModifierInstance> list = Arrays.stream(modifier).map(ModifierInstance::new).toList();
            this.randomModifiers.addAll(list);
            return this;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        @Override
        public RegistryInfo<IQuality> getRegistryType() {
            return ModKubeJSPlugin.QUALITY;
        }

        @Override
        public IQuality createObject() {
            return new KubeJSQuality(this);
        }
    }
}
