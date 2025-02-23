package com.xiaohunao.equipment_benediction.common.equipment_set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaohunao.equipment_benediction.common.context.AttackEntityContext;
import com.xiaohunao.equipment_benediction.common.hook.hooks.EquipEquipmentHook;
import com.xiaohunao.equipment_benediction.common.hook.hooks.LivingIncomingDamageHook;
import com.xiaohunao.equipment_benediction.common.hook.hooks.MobEffectApplicableHook;
import com.xiaohunao.equipment_benediction.common.hook.hooks.UnequipEquipmentHook;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.List;
import java.util.Map;


public class WearBonus implements EquipEquipmentHook, UnequipEquipmentHook, MobEffectApplicableHook, LivingIncomingDamageHook {
    private final Map<Holder<Attribute>,AttributeModifier> attributes;
    private final List<MobEffectInstance> mobEffectInstances;
    private final List<Holder<DamageType>> damageTypes;
    private final List<Holder<MobEffect>> mobEffects;

    public WearBonus(Map<Holder<Attribute>, AttributeModifier> attributes, List<MobEffectInstance> mobEffectInstances, List<Holder<DamageType>> damageTypes, List<Holder<MobEffect>> mobEffects) {
        this.attributes = attributes;
        this.mobEffectInstances = mobEffectInstances;
        this.damageTypes = damageTypes;
        this.mobEffects = mobEffects;
    }

    @Override
    public void onEquipEquipment(Player player) {
        attributes.forEach((attribute, attributeModifier) -> {
            AttributeInstance attributeInstance = player.getAttribute(attribute);
            if (attributeInstance != null) {
                attributeInstance.addTransientModifier(attributeModifier);
            }
        });

        mobEffectInstances.forEach(effectInstance -> {
            player.addEffect(new MobEffectInstance(effectInstance));
        });

    }

    @Override
    public void onUnequipEquipment(Player player) {
        attributes.forEach((attribute, attributeModifier) -> {
            AttributeInstance attributeInstance = player.getAttribute(attribute);
            if (attributeInstance != null) {
                attributeInstance.removeModifier(attributeModifier);
            }
        });

        mobEffectInstances.forEach(effectInstance -> {
            player.removeEffect(effectInstance.getEffect());
        });
    }

    @Override
    public boolean onLivingIncomingDamage(AttackEntityContext attackEntityContext) {
        DamageContainer damageContainer = attackEntityContext.damageContainer();
        for (Holder<DamageType> type : damageTypes) {
            if (damageContainer.getSource().typeHolder().is(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MobEffectEvent.Applicable.Result onMobEffectApplicable(Entity entity, MobEffectInstance effectInstance) {
        for (Holder<MobEffect> effect : mobEffects) {
            if (effect.is(effectInstance.getEffect())) {
                return MobEffectEvent.Applicable.Result.DO_NOT_APPLY;
            }
        }
        return MobEffectEvent.Applicable.Result.DEFAULT;
    }

    public static class Builder {
        private final Map<Holder<Attribute>,AttributeModifier> attributes = Maps.newHashMap();
        private final List<MobEffectInstance> mobEffectInstances = Lists.newArrayList();
        private final List<Holder<DamageType>> damageTypes = Lists.newArrayList();
        private final List<Holder<MobEffect>> mobEffects = Lists.newArrayList();

        public Builder addBonus(Holder<Attribute> attribute, AttributeModifier attributeModifier) {
            attributes.put(attribute, attributeModifier);
            return this;
        }

        public Builder addBonus(MobEffectInstance mobEffectInstance) {
            mobEffectInstances.add(mobEffectInstance);
            return this;
        }

        public Builder addDamageTypeImmunity(Holder<DamageType> damageType) {
            damageTypes.add(damageType);
            return this;
        }

        public Builder addMobEffectImmunityBonus(Holder<MobEffect> mobEffect) {
            mobEffects.add(mobEffect);
            return this;
        }

        public WearBonus build() {
            return new WearBonus(attributes, mobEffectInstances, damageTypes, mobEffects);
        }
    }

}
