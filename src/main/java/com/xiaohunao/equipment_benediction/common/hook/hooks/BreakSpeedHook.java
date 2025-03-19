package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public interface BreakSpeedHook extends IHook {
    Float onBreakSpeed(IBenediction owner, Player entity, BlockState state, float originalSpeed);
}
