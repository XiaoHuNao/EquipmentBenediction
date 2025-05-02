package com.xiaohunao.equipment_benediction.common.hook.special;

import com.xiaohunao.equipment_benediction.common.hook.HookType;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import net.minecraft.world.entity.player.Player;

public class SpecialCDHook extends SpecialTimeHook{
    public SpecialCDHook(HookType<?> hookType, IHook hook, long time) {
        super(hookType, hook, time);
    }

    @Override
    public boolean canExecuteHook(SpecialTimeHookManager specialTimeHookManager, Player player, SpecialTimeHookWrapper wrapper, long remainingTime) {
        return false;
    }

}
