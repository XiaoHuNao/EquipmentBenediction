package com.xiaohunao.equipment_benediction.common.hook.special;

import com.xiaohunao.equipment_benediction.common.attachment.EntityHookManager;
import com.xiaohunao.equipment_benediction.common.hook.HookType;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class SpecialTimerHook extends SpecialTimeHook{

    public SpecialTimerHook(HookType<?> hookType, IHook hook, long time) {
        super(hookType, hook, time);
    }

    @Override
    public boolean canExecuteHook(SpecialTimeHookManager specialTimeHookManager, Player player, SpecialTimeHookWrapper wrapper, Long remainingTime) {
        IBenediction owner = wrapper.owner();
        IHook hook = wrapper.specialTimeHook().getHook();
        Map<SpecialTimeHookWrapper, Long> specialTimeHooks = specialTimeHookManager.getSpecialTimeHooks();
        EntityHookManager entityHookManager = player.getData(EBAttachments.ENTITY_HOOK_MANAGER);
        long remainingDelay = remainingTime - 1;
        if (remainingDelay <= 0) {
            specialTimeHooks.put(wrapper, wrapper.specialTimeHook().getTime());
            return true;
        } else {
            specialTimeHooks.put(wrapper, remainingDelay);
        }
        return false;
    }

}
