package com.xiaohunao.equipment_benediction.compat.kubejs;

import com.xiaohunao.equipment_benediction.api.IEquippable;
import com.xiaohunao.equipment_benediction.api.IModifier;
import com.xiaohunao.equipment_benediction.common.bonus.BonusHandler;
import com.xiaohunao.equipment_benediction.common.equipment_set.equippable.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierInstance;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Predicate;

public class KubeJSModifier extends Modifier {
    public KubeJSModifier(Builder builder) {
        super(builder.rarity, builder.level);
        this.handler = builder.handler;
        this.color = builder.color;
        this.group = builder.group;
        this.isViable = builder.isViable;
    }


    @Override
    public void init(BonusHandler<ModifierInstance> handle, EquippableGroup group) {

    }

    public static class Builder extends BonusHandlerBuilder<IModifier,ModifierInstance>{
        private final EquippableGroup group = EquippableGroup.create();
        private Predicate<ItemStack> isViable = stack -> true;
        private int color = 0x7a7b78;
        private int rarity;
        private int level;

        public Builder(ResourceLocation location) {
            super(location);
        }
        public Builder properties(int rarity, int level) {
            this.rarity = rarity;
            this.level = level;
            return this;
        }
        public Builder addGroup(IEquippable<?> equippable, Ingredient ingredient) {
            this.group.addGroup(equippable, ingredient);
            return this;
        }
        public Builder addBlacklist(IEquippable<?> equippable) {
            this.group.addBlacklist(equippable);
            return this;
        }
        public Builder isViable(Predicate<ItemStack> isViable) {
            this.isViable = isViable;
            return this;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        @Override
        public RegistryInfo<IModifier> getRegistryType() {
            return ModKubeJSPlugin.MODIFIER;
        }

        @Override
        public IModifier createObject() {
            return new KubeJSModifier(this);
        }
    }
}
