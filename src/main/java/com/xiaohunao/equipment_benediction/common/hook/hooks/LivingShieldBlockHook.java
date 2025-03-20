package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

public interface LivingShieldBlockHook extends IHook {
    void onLivingShieldBlock(IBenediction owner, LivingShieldBlockEvent event);
}
