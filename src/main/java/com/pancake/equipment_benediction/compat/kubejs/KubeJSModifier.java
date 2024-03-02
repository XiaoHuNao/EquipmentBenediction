package com.pancake.equipment_benediction.compat.kubejs;

import com.pancake.equipment_benediction.api.IModifier;
import com.pancake.equipment_benediction.common.bonus.BonusHandler;
import com.pancake.equipment_benediction.common.modifier.Modifier;
import com.pancake.equipment_benediction.common.modifier.ModifierInstance;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class KubeJSModifier extends Modifier {
    public KubeJSModifier(Builder builder) {
        super();
        this.handler = builder.handler;
        this.textColor = builder.textColor;
    }


    @Override
    public void init(BonusHandler<ModifierInstance> handle) {
    }

    public static class Builder extends BonusHandlerBuilder<IModifier,ModifierInstance>{
        private int textColor;

        public Builder(ResourceLocation location) {
            super(location);
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
