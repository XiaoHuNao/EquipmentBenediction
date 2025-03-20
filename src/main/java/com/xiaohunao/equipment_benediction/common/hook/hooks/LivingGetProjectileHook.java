package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.neoforged.neoforge.event.entity.living.LivingGetProjectileEvent;

public interface LivingGetProjectileHook extends IHook {
    void onLivingGetProjectile(IBenediction owner, LivingGetProjectileEvent event);
}
