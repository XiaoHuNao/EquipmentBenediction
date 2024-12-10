package com.xiaohunao.equipment_benediction.common.bonus;

import com.mojang.serialization.MapCodec;
import com.xiaohunao.equipment_benediction.common.init.EQMapCodecs;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;

public record MobEffectImmunitiesBonus(HolderSet<MobEffect> mobEffects) implements IBonus{
    public final static MapCodec<MobEffectImmunitiesBonus> CODEC = RegistryCodecs.homogeneousList(Registries.MOB_EFFECT)
            .fieldOf("bonus").xmap(MobEffectImmunitiesBonus::new,MobEffectImmunitiesBonus::mobEffects);

    @Override
    public void apply(Player player) {

    }

    @Override
    public void clear(Player player) {

    }

    @Override
    public MapCodec<? extends IBonus> mapCodec() {
        return EQMapCodecs.MOB_EFFECT_IMMUNITIES_BONUS_CODEC.get();
    }
}
