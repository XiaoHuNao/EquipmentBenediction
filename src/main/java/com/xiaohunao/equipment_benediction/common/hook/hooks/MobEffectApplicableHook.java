package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public interface MobEffectApplicableHook extends IHook {
    void onMobEffectApplicable(IBenediction Owner, MobEffectEvent.Applicable event);
}
