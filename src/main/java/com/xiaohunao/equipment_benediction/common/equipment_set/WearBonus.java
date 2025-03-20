package com.xiaohunao.equipment_benediction.common.equipment_set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaohunao.equipment_benediction.common.context.LivingEquipmentChangeContext;
import com.xiaohunao.equipment_benediction.common.hook.hooks.EquipEquipmentHook;
import com.xiaohunao.equipment_benediction.common.hook.hooks.LivingIncomingDamageHook;
import com.xiaohunao.equipment_benediction.common.hook.hooks.MobEffectApplicableHook;
import com.xiaohunao.equipment_benediction.common.hook.hooks.UnequipEquipmentHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.List;
import java.util.Map;


public class WearBonus implements EquipEquipmentHook, UnequipEquipmentHook, MobEffectApplicableHook, LivingIncomingDamageHook {
    protected final Map<Holder<Attribute>, AttributeModifier> attributes;
    protected final List<MobEffectInstance> mobEffectInstances;
    protected final List<ResourceKey<DamageType>> damageTypeResourceKeys;
    protected final List<TagKey<DamageType>> damageTypeTagKeys;
    protected final List<Holder<MobEffect>> mobEffects;

    public WearBonus(Map<Holder<Attribute>, AttributeModifier> attributes, List<MobEffectInstance> mobEffectInstances, List<ResourceKey<DamageType>> damageTypeResourceKeys, List<TagKey<DamageType>> damageTypeTagKeys, List<Holder<MobEffect>> mobEffects) {
        this.attributes = attributes;
        this.mobEffectInstances = mobEffectInstances;
        this.damageTypeResourceKeys = damageTypeResourceKeys;
        this.damageTypeTagKeys = damageTypeTagKeys;
        this.mobEffects = mobEffects;
    }

    @Override
    public void onEquipEquipment(IBenediction owner, LivingEquipmentChangeContext changeContext) {
        attributes.forEach((attribute, attributeModifier) -> {
            AttributeInstance attributeInstance = changeContext.livingEntity().getAttribute(attribute);
            if (attributeInstance != null) {
                attributeInstance.addOrUpdateTransientModifier(attributeModifier);
            }
        });

        mobEffectInstances.forEach(effectInstance -> {
            changeContext.livingEntity().addEffect(new MobEffectInstance(effectInstance));
        });

    }

    @Override
    public void onUnequipEquipment(IBenediction owner, LivingEquipmentChangeContext changeContext) {
        attributes.forEach((attribute, attributeModifier) -> {
            AttributeInstance attributeInstance = changeContext.livingEntity().getAttribute(attribute);
            if (attributeInstance != null) {
                attributeInstance.removeModifier(attributeModifier);
            }
        });

        mobEffectInstances.forEach(effectInstance -> {
            changeContext.livingEntity().removeEffect(effectInstance.getEffect());
        });
    }

    @Override
    public void onLivingIncomingDamage(IBenediction owner, LivingIncomingDamageEvent event) {
        DamageContainer damageContainer = event.getContainer();
        for (ResourceKey<DamageType> type : damageTypeResourceKeys) {
            if (damageContainer.getSource().is(type)) {
                event.setCanceled(true);
                return;
            }
        }

        for (TagKey<DamageType> type : damageTypeTagKeys) {
            if (damageContainer.getSource().is(type)) {
                event.setCanceled(true);
                return;
            }
        }
    }

    @Override
    public void onMobEffectApplicable(IBenediction owner, MobEffectEvent.Applicable event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        for (Holder<MobEffect> effect : mobEffects) {
            if (effect.is(effectInstance.getEffect())) {
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
                return;
            }
        }
    }


    public static class Builder {
        private final Map<Holder<Attribute>, AttributeModifier> attributes = Maps.newHashMap();
        private final List<MobEffectInstance> mobEffectInstances = Lists.newArrayList();
        private final List<ResourceKey<DamageType>> damageTypeResourceKeys = Lists.newArrayList();
        private final List<TagKey<DamageType>> damageTypeTagKeys = Lists.newArrayList();
        private final List<Holder<MobEffect>> mobEffects = Lists.newArrayList();

        public Builder addBonus(Holder<Attribute> attribute, AttributeModifier attributeModifier) {
            attributes.put(attribute, attributeModifier);
            return this;
        }

        public Builder addBonus(MobEffectInstance mobEffectInstance) {
            mobEffectInstances.add(mobEffectInstance);
            return this;
        }

        public Builder addBonus(MobEffectInstance... mobEffectInstance) {
            mobEffectInstances.addAll(List.of(mobEffectInstance));
            return this;
        }

        public Builder addDamageTypeImmunity(ResourceKey<DamageType> damageType) {
            damageTypeResourceKeys.add(damageType);
            return this;
        }

        public Builder addDamageTypeImmunity(TagKey<DamageType> damageType) {
            damageTypeTagKeys.add(damageType);
            return this;
        }

        public Builder addMobEffectImmunityBonus(Holder<MobEffect> mobEffect) {
            mobEffects.add(mobEffect);
            return this;
        }

        public WearBonus build() {
            return new WearBonus(attributes, mobEffectInstances, damageTypeResourceKeys, damageTypeTagKeys, mobEffects);
        }
    }

}
