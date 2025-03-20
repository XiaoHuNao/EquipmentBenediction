package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.world.entity.LivingEntity;

public interface LivingHealHook extends IHook {
    Float onLivingHeal(IBenediction owner, LivingEntity living, float original);
}
