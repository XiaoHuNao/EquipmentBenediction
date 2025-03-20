package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

public interface LivingHealHook extends IHook {
    void onLivingHeal(IBenediction owner, LivingHealEvent event);
}
