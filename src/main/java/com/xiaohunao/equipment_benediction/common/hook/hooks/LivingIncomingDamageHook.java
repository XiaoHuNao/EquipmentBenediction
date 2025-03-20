package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public interface LivingIncomingDamageHook extends IHook {
    void onLivingIncomingDamage(IBenediction owner, LivingIncomingDamageEvent event);
}
