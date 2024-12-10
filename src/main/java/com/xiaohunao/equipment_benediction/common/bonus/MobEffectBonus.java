package com.xiaohunao.equipment_benediction.common.bonus;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.equipment_benediction.common.init.EQMapCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public record MobEffectBonus(Holder<MobEffect> mobEffect,MobEffectInstance mobEffectInstance) implements IBonus {
    public final static MapCodec<MobEffectBonus> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("mobEffect").forGetter(MobEffectBonus::mobEffect),
            MobEffectInstance.CODEC.fieldOf("mobEffectInstance").forGetter(MobEffectBonus::mobEffectInstance)
    ).apply(instance, MobEffectBonus::new));

    @Override
    public void apply(Player player) {
        player.addEffect(new MobEffectInstance(mobEffectInstance),player);
    }

    @Override
    public void clear(Player player) {
        player.removeEffect(mobEffect);
    }

    @Override
    public MapCodec<? extends IBonus> mapCodec() {
        return EQMapCodecs.MOB_EFFECT_BONUS_CODEC.get();
    }
}
