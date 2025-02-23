package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public interface MobEffectApplicableHook extends IHook {
    MobEffectEvent.Applicable.Result onMobEffectApplicable(Entity entity, MobEffectInstance effectInstance);
}
