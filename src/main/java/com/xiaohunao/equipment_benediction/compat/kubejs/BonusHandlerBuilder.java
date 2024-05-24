package com.xiaohunao.equipment_benediction.compat.kubejs;

import com.xiaohunao.equipment_benediction.common.bonus.BonusHandler;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class BonusHandlerBuilder<T,N> extends BuilderBase<T> {
    protected final BonusHandler<N> handler = new BonusHandler<>();
    public BonusHandlerBuilder(ResourceLocation location) {
        super(location);
    }

    public BonusHandlerBuilder<T,N> addEffectModifier(MobEffect effect, int duration, int amplifier) {
        this.handler.addEffectModifier(effect, duration, amplifier);
        return this;
    }

    public BonusHandlerBuilder<T,N> addAttributeModifier(Attribute attribute, String uuid, double value, AttributeModifier.Operation operation) {
        this.handler.addAttributeModifier(attribute, uuid, value, operation);
        return this;
    }

    public BonusHandlerBuilder<T,N> addEvent(String eventClass, Consumer<?> consumer) {
        this.handler.addEvent(eventClass, consumer);
        return this;
    }

    public BonusHandlerBuilder<T,N> addTick(BiConsumer<Player, N> consumer) {
        this.handler.addTick(consumer);
        return this;
    }

}
