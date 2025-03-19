package com.xiaohunao.equipment_benediction.common.hook.dynamic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.equipment_benediction.common.hook.hooks.MobEffectApplicableHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.List;

public record MobEffectImmunityHook(List<Holder<MobEffect>> effects) implements ISerializableHook, MobEffectApplicableHook {
    public static final MapCodec<MobEffectImmunityHook> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MobEffect.CODEC.listOf().fieldOf("effects").forGetter(MobEffectImmunityHook::effects)
    ).apply(instance, MobEffectImmunityHook::new));

    @Override
    public MobEffectEvent.Applicable.Result onMobEffectApplicable(IBenediction owner, Entity entity, MobEffectInstance effectInstance) {
        for (Holder<MobEffect> effect : effects) {
            if (effect.is(effectInstance.getEffect())) {
                return MobEffectEvent.Applicable.Result.DO_NOT_APPLY;
            }
        }
        return MobEffectEvent.Applicable.Result.DEFAULT;
    }
}
