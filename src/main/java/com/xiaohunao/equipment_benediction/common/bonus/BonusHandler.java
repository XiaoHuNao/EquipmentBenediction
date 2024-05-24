package com.xiaohunao.equipment_benediction.common.bonus;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.xiaohunao.equipment_benediction.api.IBonus;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BonusHandler<T> {
    private Multimap<String, IBonus> bonuses = ArrayListMultimap.create();
    private BiConsumer<Player, T> tickBonus = (player, t) -> {};

    public BonusHandler<T> addEffectModifier(MobEffect effect, int duration, int amplifier) {
        this.bonuses.put("effect", new MobEffectBonus(effect,new MobEffectInstance(effect, duration, amplifier)));
        return this;
    }

    public BonusHandler<T> addAttributeModifier(Attribute attribute, String uuid, double value, AttributeModifier.Operation operation) {
        this.bonuses.put("attribute", new AttributeBonus(attribute, new AttributeModifier(UUID.fromString(uuid), () -> "ModifierHandler", value, operation)));
        return this;
    }

    public <E> BonusHandler<T> addEvent(Class<E> eventClass, Consumer<E> consumer) {
        this.bonuses.put("event", new EventBonus(eventClass, consumer));
        return this;
    }

    public BonusHandler<T> addEvent(String eventClass, Consumer<?> consumer) {
        try {
            Class<?> type = Class.forName(eventClass);
            this.bonuses.put("event", new EventBonus(type, consumer));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public BonusHandler<T> addTick(BiConsumer<Player, T> consumer) {
        this.tickBonus = consumer;
        return this;
    }

    public BiConsumer<Player, T> getTickBonus() {
        return this.tickBonus;
    }

    public Multimap<String, IBonus> getBonuses() {
        return this.bonuses;
    }

    public Collection<IBonus> getBonus(String type) {
        return this.bonuses.get(type);
    }
}
