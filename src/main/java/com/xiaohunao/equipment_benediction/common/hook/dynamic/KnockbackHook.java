package com.xiaohunao.equipment_benediction.common.hook.dynamic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.equipment_benediction.common.context.AttackEntityContext;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.hook.hooks.BeforeMeleeHitHook;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public class KnockbackHook implements ISerializableHook, BeforeMeleeHitHook {
    public static final MapCodec<KnockbackHook> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("knockback",0.5f).forGetter(KnockbackHook::knockback),
            Codec.FLOAT.optionalFieldOf("vertical_factor", 0.3f).forGetter(KnockbackHook::verticalFactor),
            Codec.FLOAT.optionalFieldOf("level_multiplier", 0.5f).forGetter(KnockbackHook::levelMultiplier),
            Codec.FLOAT.optionalFieldOf("base_vertical", 0.4f).forGetter(KnockbackHook::baseVertical),
            Codec.FLOAT.optionalFieldOf("max_vertical", 0.8f).forGetter(KnockbackHook::maxVertical)
    ).apply(instance, KnockbackHook::new));

    private float knockback;
    private float verticalFactor;
    private float levelMultiplier;
    private float baseVertical;
    private float maxVertical;

    public KnockbackHook(float knockback, float verticalFactor, float levelMultiplier,
                         float baseVertical, float maxVertical) {
        this.knockback = knockback;
        this.verticalFactor = verticalFactor;
        this.levelMultiplier = levelMultiplier;
        this.baseVertical = baseVertical;
        this.maxVertical = maxVertical;
    }

    public KnockbackHook() {
        this(0.5f, 0.3f, 0.5f, 0.4f, 0.8f);
    }

    public float knockback() {
        return knockback;
    }

    public KnockbackHook setKnockback(float knockback) {
        this.knockback = knockback;
        return this;
    }

    public float verticalFactor() {
        return verticalFactor;
    }

    public KnockbackHook setVerticalFactor(float verticalFactor) {
        this.verticalFactor = verticalFactor;
        return this;
    }

    public float levelMultiplier() {
        return levelMultiplier;
    }

    public KnockbackHook setLevelMultiplier(float levelMultiplier) {
        this.levelMultiplier = levelMultiplier;
        return this;
    }

    public float baseVertical() {
        return baseVertical;
    }

    public KnockbackHook setBaseVertical(float baseVertical) {
        this.baseVertical = baseVertical;
        return this;
    }

    public float maxVertical() {
        return maxVertical;
    }

    public KnockbackHook setMaxVertical(float maxVertical) {
        this.maxVertical = maxVertical;
        return this;
    }

    @Override
    public void beforeMeleeHit(IBenediction owner, AttackEntityContext attackEntityContext) {
//        Entity hitEntity = attackEntityContext.hitEntity();
//        Entity attacker = attackEntityContext.damageContainer().getSource().getEntity();
//
//        if (!(hitEntity instanceof LivingEntity livingEntity) || attacker == null) {
//            return;
//        }
//
//        // 计算等级加成和击退效果
//        float levelBonus = 1.0f + (modifierInstance.getLevel() - 1) * levelMultiplier;
//        applyNormalKnockback(livingEntity, attacker, knockback * levelBonus);
//        applyVerticalKnockback(livingEntity, modifierInstance.getLevel());
    }

    private void applyNormalKnockback(LivingEntity target, Entity attacker, float strength) {
        // 获取攻击者的击退属性
        float attackerKnockback = attacker instanceof LivingEntity livingAttacker 
            ? (float) livingAttacker.getAttributeValue(Attributes.ATTACK_KNOCKBACK) 
            : 0.0f;

        float combinedKnockback = strength + attackerKnockback;
        if (combinedKnockback <= 0) {
            return;
        }

        double xDiff = target.getX() - attacker.getX();
        double zDiff = target.getZ() - attacker.getZ();
        
        double distance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        if (distance > 0) {
            xDiff /= distance;
            zDiff /= distance;

            double horizontalStrength = combinedKnockback * 0.5;
            double verticalStrength = Math.min(maxVertical, baseVertical + (combinedKnockback * verticalFactor));

            target.setDeltaMovement(
                target.getDeltaMovement().add(
                    xDiff * horizontalStrength,
                    verticalStrength,
                    zDiff * horizontalStrength
                )
            );
            target.hasImpulse = true;
        }
    }

    private void applyVerticalKnockback(LivingEntity target, int level) {
        double upwardSpeed = baseVertical + (level - 1) * verticalFactor;
        target.setDeltaMovement(target.getDeltaMovement().add(0, upwardSpeed, 0));
        target.hasImpulse = true;
    }
}
