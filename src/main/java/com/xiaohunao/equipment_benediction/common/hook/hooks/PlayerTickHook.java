package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.world.entity.player.Player;

public interface PlayerTickHook extends IHook {
    void onPlayerTick(IBenediction owner, Player player);
}
