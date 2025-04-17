package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface AfterLivingHurtEntityHook extends IHook {
    void afterLivingHurtEntity(IBenediction owner, Data data);

    record Data(Player attacker, LivingEntity victim, DamageSource damageSource, float amount) {}
}
