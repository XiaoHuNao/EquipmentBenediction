package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public interface MobEffectApplicableHook extends IHook {
    MobEffectEvent.Applicable.Result onMobEffectApplicable(IBenediction Owner, Entity entity, MobEffectInstance effectInstance);
}
