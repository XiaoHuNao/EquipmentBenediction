package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface BeforeLivingDamageHook extends IHook {
    void beforeLivingDamage(IBenediction owner, LivingEntity victim, DamageSource damageSource);
}
