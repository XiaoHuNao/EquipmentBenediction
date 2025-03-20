package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;

public interface LivingBreatheHook extends IHook {
    void onLivingBreathe(IBenediction owner, LivingBreatheEvent event);
}
