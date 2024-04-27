package com.pancake.equipment_benediction.compat.kubejs;

import com.pancake.equipment_benediction.api.IEquippable;
import com.pancake.equipment_benediction.api.IModifier;
import com.pancake.equipment_benediction.common.bonus.BonusHandler;
import com.pancake.equipment_benediction.common.equippable.EquippableGroup;
import com.pancake.equipment_benediction.common.modifier.Modifier;
import com.pancake.equipment_benediction.common.modifier.ModifierInstance;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class KubeJSModifier extends Modifier {
    public KubeJSModifier(Builder builder) {
        super();
        this.handler = builder.handler;
        this.textColor = builder.textColor;
    }


    @Override
    public void init(BonusHandler<ModifierInstance> handle, EquippableGroup group) {

    }

    public static class Builder extends BonusHandlerBuilder<IModifier,ModifierInstance>{
        public EquippableGroup group = EquippableGroup.create();
        private int textColor;

        public Builder(ResourceLocation location) {
            super(location);
        }
        public Builder addGroup(IEquippable<?> equippable, Ingredient ingredient) {
            this.group.addGroup(equippable, ingredient);
            return this;
        }
        public Builder addGroup(IEquippable<?> equippable, Item item) {
            return addGroup(equippable, Ingredient.of(item));
        }
        public Builder addGroup(IEquippable<?> equippable, Item... items) {
            return addGroup(equippable, Ingredient.of(items));
        }
        public Builder addGroup(IEquippable<?> equippable, TagKey<Item> tagKey) {
            return addGroup(equippable, Ingredient.of(tagKey));
        }
        public Builder addBlacklist(IEquippable<?> equippable) {
            this.group.addBlacklist(equippable);
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
        public Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }
    }
}
