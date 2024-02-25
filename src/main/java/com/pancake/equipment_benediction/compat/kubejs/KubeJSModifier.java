package com.pancake.equipment_benediction.compat.kubejs;

import com.pancake.equipment_benediction.api.ForgeEventConsumer;
import com.pancake.equipment_benediction.api.IModifier;
import com.pancake.equipment_benediction.common.modifier.Modifier;
import com.pancake.equipment_benediction.common.modifier.ModifierHandler;
import com.pancake.equipment_benediction.common.modifier.ModifierInstance;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public class KubeJSModifier extends Modifier {
    public KubeJSModifier(Builder builder) {
        super();
        this.handler = builder.handler.build();
        this.textColor = builder.textColor;
    }


    @Override
    public void init(ModifierHandler.Builder handle) {
    }

    public static class Builder extends BuilderBase<IModifier>{
        private ModifierHandler.Builder handler;
        private int textColor;

        public Builder(ResourceLocation location) {
            super(location);
            this.handler = new ModifierHandler.Builder();
        }

        @Override
        public RegistryInfo<IModifier> getRegistryType() {
            return ModKubeJSPlugin.MODIFIER;
        }

        @Override
        public IModifier createObject() {
            return new KubeJSModifier(this);
        }
        public Builder addEffectModifier(MobEffect effect, int duration, int amplifier) {
            this.handler.addEffectModifier(effect, duration, amplifier);
            return this;
        }

        public Builder addAttributeModifier(Attribute attribute, String uuid, double value, AttributeModifier.Operation operation) {
            this.handler.addAttributeModifier(attribute, uuid, value, operation);
            return this;
        }

        public Builder addEvent(String eventClass, ForgeEventConsumer<?> consumer) {
            this.handler.addEvent(eventClass, consumer);
            return this;
        }

        public Builder addTick(BiConsumer<Player, ModifierInstance> consumer) {
            this.handler.addTick(consumer);
            return this;
        }

        public Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }
    }
}
