package com.pancake.equipment_benediction.compat.kubejs;

import com.google.common.collect.Lists;
import com.pancake.equipment_benediction.api.IQuality;
import com.pancake.equipment_benediction.common.modifier.ModifierInstance;
import com.pancake.equipment_benediction.common.quality.Quality;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.function.Predicate;

public class KubeJSQuality extends Quality {
    public KubeJSQuality(Builder builder) {
        super(builder.recastingStack, builder.rarity, builder.level, builder.maxModifierCount);
        this.isViable = builder.isViable;
        this.modifiers.addAll(builder.modifiers);
        this.color = builder.color;
    }

    public static class Builder extends BuilderBase<IQuality> {
        private final List<ModifierInstance> modifiers = Lists.newArrayList();
        private Predicate<ItemStack> isViable;
        private Ingredient recastingStack;
        private int rarity;
        private int level;
        private int maxModifierCount;
        private int color = 0x7a7b78;

        public Builder(ResourceLocation location) {
            super(location);
        }

        public Builder recastingStack(Ingredient recastingStack) {
            this.recastingStack = recastingStack;
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
        public Builder addFixedModifier(ModifierInstance modifier) {
            if (this.modifiers.size() < maxModifierCount) {
                this.modifiers.add(modifier);
            }
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
