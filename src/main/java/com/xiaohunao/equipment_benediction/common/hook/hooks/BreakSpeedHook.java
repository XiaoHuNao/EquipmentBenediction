package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public interface BreakSpeedHook extends IHook {
    void onBreakSpeed(IBenediction owner, PlayerEvent.BreakSpeed event);
}
