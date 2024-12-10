package com.xiaohunao.equipment_benediction.common.bonus;

import com.mojang.serialization.MapCodec;
import com.xiaohunao.equipment_benediction.common.init.EQMapCodecs;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;

public record DamageTypesImmunitiesBonus(HolderSet<DamageType> damageTypes) implements IBonus{
    public final static MapCodec<DamageTypesImmunitiesBonus> CODEC = RegistryCodecs.homogeneousList(Registries.DAMAGE_TYPE)
            .fieldOf("bonus").xmap(DamageTypesImmunitiesBonus::new,DamageTypesImmunitiesBonus::damageTypes);

    @Override
    public void apply(Player player) {

    }

    @Override
    public void clear(Player player) {

    }

    @Override
    public MapCodec<? extends IBonus> mapCodec() {
        return EQMapCodecs.DAMAGE_IMMUNITIES_BONUS_CODEC.get();
    }
}
