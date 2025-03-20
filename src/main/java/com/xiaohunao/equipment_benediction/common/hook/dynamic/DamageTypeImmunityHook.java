package com.xiaohunao.equipment_benediction.common.hook.dynamic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.equipment_benediction.common.hook.hooks.LivingIncomingDamageHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

public record DamageTypeImmunityHook(List<Holder<DamageType>> damageTypes) implements ISerializableHook, LivingIncomingDamageHook {
    public static final MapCodec<DamageTypeImmunityHook> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DamageType.CODEC.listOf().fieldOf("damageTypes").forGetter(DamageTypeImmunityHook::damageTypes)
    ).apply(instance, DamageTypeImmunityHook::new));

    @Override
    public void onLivingIncomingDamage(IBenediction owner, LivingIncomingDamageEvent event) {
        DamageContainer damageContainer = event.getContainer();
        for (Holder<DamageType> type : damageTypes) {
            if (damageContainer.getSource().typeHolder().is(type)) {
                event.setCanceled(true);
                return;
            }
        }
    }
}
