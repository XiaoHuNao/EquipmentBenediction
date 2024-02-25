package com.pancake.equipment_benediction.common.modifier;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pancake.equipment_benediction.api.ForgeEventConsumer;
import com.pancake.equipment_benediction.api.IModifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;


public class ModifierHandler implements Supplier {
    private Map<Attribute, AttributeModifier> attributeModifiers = Maps.newHashMap();
    private Map<MobEffect, MobEffectInstance> effectModifiers = Maps.newHashMap();
    private List<EventInfo> eventConsumers = Lists.newArrayList();
    private BiConsumer<Player, ModifierInstance> tickConsumer = (player, instance) -> {};

    public ModifierHandler addEffectModifier(MobEffect effect, int duration, int amplifier) {
        this.effectModifiers.put(effect, new MobEffectInstance(effect, duration, amplifier));
        return this;
    }

    public void removeEffectModifier(MobEffect effect) {
        this.effectModifiers.remove(effect);
    }

    public Map<MobEffect, MobEffectInstance> getEffectModifiers() {
        return this.effectModifiers;
    }

    public ModifierHandler addAttributeModifier(Attribute attribute, String uuid, double value, AttributeModifier.Operation operation) {
        AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(uuid), () -> "ModifierHandler", value, operation);
        this.attributeModifiers.put(attribute, attributemodifier);
        return this;
    }

    public void removeAttributeModifiers(Attribute attribute) {
        this.attributeModifiers.remove(attribute);
    }

    public Map<Attribute, AttributeModifier> getAttributeModifiers() {
        return this.attributeModifiers;
    }

    public <T> ModifierHandler addEvent(Class<T> eventClass, ForgeEventConsumer<T> consumer) {
        eventConsumers.add(new EventInfo(eventClass, consumer));
        return this;
    }

    public void addEvent(String eventClass, ForgeEventConsumer<?> consumer) {
        try {
            Class<?> type = Class.forName(eventClass);
            eventConsumers.add(new EventInfo(type, consumer));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void addTick(BiConsumer<Player, ModifierInstance> consumer) {
        this.tickConsumer = consumer;
    }

    public List<EventInfo> getEventConsumers() {
        return eventConsumers;
    }

    public BiConsumer<Player, ModifierInstance> getTickConsumer() {
        return tickConsumer;
    }

    @Override
    public Object get() {
        return this;
    }


    public static class Builder {
        private final ModifierHandler handler = new ModifierHandler();
        public Builder addEffectModifier(MobEffect effect, int duration, int amplifier) {
            handler.addEffectModifier(effect, duration, amplifier);
            return this;
        }

        public Builder addAttributeModifier(Attribute attribute, String uuid, double value, AttributeModifier.Operation operation) {
            handler.addAttributeModifier(attribute, uuid, value, operation);
            return this;
        }
        public <T> Builder addEvent(Class<T> eventClass, ForgeEventConsumer<T> consumer) {
            handler.addEvent(eventClass, consumer);
            return this;
        }

        public Builder addEvent(String eventClass, ForgeEventConsumer consumer) {
            handler.addEvent(eventClass, consumer);
            return this;
        }

        public Builder addTick(BiConsumer<Player, ModifierInstance> consumer) {
            handler.addTick(consumer);
            return this;
        }

        public ModifierHandler build() {
            return handler;
        }
    }

    public record EventInfo(Class eventClass, ForgeEventConsumer consumer) {}



}
