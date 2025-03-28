package com.xiaohunao.equipment_benediction.common.hook.special;

import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.equipment_benediction.common.hook.HookType;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public abstract class SpecialTimeHook implements IHook {
    private final HookType<?> hookType;
    private final IHook hook;
    private final long time;

    public SpecialTimeHook(HookType<?> hookType, IHook hook, long time) {
        this.hookType = hookType;
        this.hook = hook;
        this.time = time;
    }

    public HookType<?> getHookType() {
        return hookType;
    }

    public IHook getHook() {
        return hook;
    }

    public long getTime() {
        return time;
    }

    public abstract boolean canExecuteHook(SpecialTimeHookManager specialTimeHookManager, Player player, SpecialTimeHookWrapper wrapper, Long remainingTime);

    public <T extends IHook, R> void executeSpecialHook(SpecialTimeHookWrapper wrapper) {
        HookMapManager.HookExecutor<T, R> executor = (HookMapManager.HookExecutor<T, R>) wrapper.executor();
        T hook = (T) wrapper.specialTimeHook().getHook();
        executor.execute(wrapper.owner(), hook, null);
    }
}
