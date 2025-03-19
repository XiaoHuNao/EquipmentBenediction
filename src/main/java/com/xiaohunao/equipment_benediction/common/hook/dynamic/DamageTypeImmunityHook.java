package com.xiaohunao.equipment_benediction.common.hook.dynamic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.equipment_benediction.common.context.AttackEntityContext;
import com.xiaohunao.equipment_benediction.common.context.DamageResultContainer;
import com.xiaohunao.equipment_benediction.common.hook.hooks.LivingIncomingDamageHook;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.damagesource.DamageContainer;

import java.util.List;

public record DamageTypeImmunityHook(List<Holder<DamageType>> damageTypes) implements ISerializableHook, LivingIncomingDamageHook {
    public static final MapCodec<DamageTypeImmunityHook> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DamageType.CODEC.listOf().fieldOf("damageTypes").forGetter(DamageTypeImmunityHook::damageTypes)
    ).apply(instance, DamageTypeImmunityHook::new));

    @Override
    public DamageResultContainer onLivingIncomingDamage(Object owner, AttackEntityContext attackEntityContext) {
        DamageContainer damageContainer = attackEntityContext.damageContainer();
        for (Holder<DamageType> type : damageTypes) {
            if (damageContainer.getSource().typeHolder().is(type)) {
                return DamageResultContainer.cancel(true);
            }
        }
        return DamageResultContainer.empty();
    }
}
