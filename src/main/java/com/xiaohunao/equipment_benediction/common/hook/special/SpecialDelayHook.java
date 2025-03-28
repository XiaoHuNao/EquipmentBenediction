package com.xiaohunao.equipment_benediction.common.hook.special;

import com.xiaohunao.equipment_benediction.common.hook.HookType;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class SpecialDelayHook extends SpecialTimeHook{
    public SpecialDelayHook(HookType<?> hookType, IHook hook, long time) {
        super(hookType, hook, time);
    }

    @Override
    public boolean canExecuteHook(SpecialTimeHookManager specialTimeHookManager, Player player, SpecialTimeHookWrapper wrapper, Long remainingTime) {
        Map<SpecialTimeHookWrapper, Long> specialTimeHooks = specialTimeHookManager.getSpecialTimeHooks();
        long remainingDelay = remainingTime - 1;
        if (remainingDelay <= 0) {
            specialTimeHooks.remove(wrapper);
            return true;
        } else {
            specialTimeHooks.put(wrapper, remainingDelay);
        }
        return false;
    }

}
